/**
 * This class represents a CPR position decoder
 *
 * @author: Bouden Omar (341381)
 * @author: Tlili Ahmed (344939)
 */
package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;

public class CprDecoder {
    private static final double ANGLES_IN_TURN_UPPER_BOUND = 0.5;
    private static final int OFFSET_RECENTER_AROUND_ZERO = 1;
    private final static int Z_PHI_0 = 60, Z_PHI_1 = 59;
    private final static double DELTA_PHI_0 = 1. / Z_PHI_0, DELTA_PHI_1 = 1. / Z_PHI_1;


    /**
     * Private CprDecoder default constructor
     */
    private CprDecoder() {
    }


    /**
     * @param x0         (double) local longitude of an even-numbered message
     * @param y0         (double) local latitude of an even-numbered message
     * @param x1         (double) local longitude of an odd-numbered message
     * @param y1         (double) local latitude of an odd-numbered message
     * @param mostRecent (int) 1 if the most recent message is odd-numbered and 0 otherwise
     * @return the geographic position corresponding to the given normalized local positions,
     * or null if the latitude of the decoded position is invalid or if the position cannot be determined
     * @throws IllegalArgumentException if mostRecent is not 0 or 1
     */
    public static GeoPos decodePosition(double x0, double y0, double x1, double y1, int mostRecent) {
        Preconditions.checkArgument(mostRecent == 0 || mostRecent == 1);
        //calculating phi
        int zPhi0 = (int) Math.rint(y0 * Z_PHI_1 - y1 * Z_PHI_0), zPhi1 = zPhi0;
        if (zPhi0 < 0) {
            zPhi0 += Z_PHI_0;
            zPhi1 += Z_PHI_1;
        }
        double phiInTurn = mostRecent == 1 ? DELTA_PHI_1 * (zPhi1 + y1) : DELTA_PHI_0 * (zPhi0 + y0);
        double otherPhiInTurn = mostRecent == 1 ? DELTA_PHI_0 * (zPhi0 + y0) : DELTA_PHI_1 * (zPhi1 + y1);
        if (phiInTurn >= ANGLES_IN_TURN_UPPER_BOUND) phiInTurn -= OFFSET_RECENTER_AROUND_ZERO;
        if (otherPhiInTurn >= ANGLES_IN_TURN_UPPER_BOUND) otherPhiInTurn -= OFFSET_RECENTER_AROUND_ZERO;


        int ZLambda0, ZLambda1;
        ZLambda0 = ZLambda0Compute(phiInTurn);

        //check for LatitudeBand switch
        if (ZLambda0 != ZLambda0Compute(otherPhiInTurn)) return null;

        ZLambda1 = ZLambda0 - 1;

        double lambdaInTurn;
        if (ZLambda0 == 1) {
            lambdaInTurn = mostRecent == 1 ? x1 : x0;
        } else {
            int zLambda = (int) Math.rint(x0 * ZLambda1 - x1 * ZLambda0);
            if (zLambda < 0) zLambda += mostRecent == 1 ? ZLambda1 : ZLambda0;
            lambdaInTurn = mostRecent == 1 ? 1. / ZLambda1 * (zLambda + x1) : 1. / ZLambda0 * (zLambda + x0);
        }
        if (lambdaInTurn >= ANGLES_IN_TURN_UPPER_BOUND) lambdaInTurn -= OFFSET_RECENTER_AROUND_ZERO;


        double lambda = Units.convert(lambdaInTurn, Units.Angle.TURN, Units.Angle.T32);
        double phi = Units.convert(phiInTurn, Units.Angle.TURN, Units.Angle.T32);


        int longitudeT32 = (int) Math.rint(lambda);
        int latitudeT32 = (int) Math.rint(phi);
        return GeoPos.isValidLatitudeT32(latitudeT32) ? new GeoPos(longitudeT32, latitudeT32) : null;

    }

    /**
     * compute value of ZLambda0 given the angle
     *
     * @param phiInTurn (double) angle in TURN
     * @return (int) ZLambda0
     */
    private static int ZLambda0Compute(double phiInTurn) {
        double phi = Units.convertFrom(phiInTurn, Units.Angle.TURN);
        double A = Math.acos(1 - (1 - Math.cos(2 * Math.PI * DELTA_PHI_0)) / (Math.cos(phi) * Math.cos(phi)));
        return (Double.isNaN(A)) ? 1 : (int) Math.floor(2 * Math.PI / A);
    }
}
