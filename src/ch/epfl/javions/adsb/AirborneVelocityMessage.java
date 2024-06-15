/**
 * This record represents an airborne velocity message (allowing aircraft to communicate its speed and direction of travel while in-flight)
 *
 * @author: Bouden Omar (341381)
 * @author: Tlili Ahmed (344939)
 */
package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.Objects;

public record AirborneVelocityMessage(long timeStampNs, IcaoAddress icaoAddress, double speed,
                                      double trackOrHeading) implements Message {
    private static final int S_TYPE_START = 48, S_TYPE_SIZE = 3;
    private static final int CONTENT_START = 21, CONTENT_SIZE = 22;
    private static final int INVALID_SPEED = -1, SPEED_SOUTH_NORTH_START = 0, SPEED_SOUTH_NORTH_SIZE = 10,
            SPEED_EAST_WEST_START = 11, SPEED_EAST_WEST_SIZE = 10;
    private static final int FIRST_S_TYPE_FOR_GROUND_BASED_POSITION = 1, SECOND_S_TYPE_FOR_GROUND_BASED_POSITION = 2,
            FIRST_S_TYPE_FOR_AIRBORNE_POSITION = 3, SECOND_S_TYPE_FOR_AIRBORNE_POSITION = 4;
    private static final int ANGLE_IN_TURN_START = 11, ANGLE_IN_TURN_SIZE = 10;
    private static final double ANGLE_IN_TURN_NORMALISATION_FACTOR = 1024.;
    private static final int IS_CAP_AVAILABLE_BIT_INDEX = 21;
    private static final int SPEED_IN_KNOT_START = 0, SPEED_IN_KNOT_SIZE = 10;
    private static final int SIGN_SOUTH_NORTH_BIT_INDEX = 10, DIRECTION_EAST_WEST_BIT_INDEX = 21;

    /**
     * Public AirborneVelocityMessage compact constructor
     *
     * @param timeStampNs    (long) the message's time stamp expressed in nanoseconds
     * @param icaoAddress    (IcaoAddress) the ICAO address of the message's sender
     * @param speed          (double) the aircraft's speed in m/s
     * @param trackOrHeading (double) the aircraft's direction of travel in radians
     * @throws NullPointerException     if the icaoAddress is null
     * @throws IllegalArgumentException if timeStamp, speed or trackHeading is strictly negative
     */
    public AirborneVelocityMessage {
        Objects.requireNonNull(icaoAddress);
        Preconditions.checkArgument(timeStampNs >= 0 && speed >= 0 && trackOrHeading >= 0);
    }

    /**
     * @param rawMessage (RawMessage)
     * @return (AirborneVelocityMessage) The airborne velocity message corresponding to the given raw message,
     * or null of the subtype is invalid, or if the speed or direction of travel cannot be determined
     */
    public static AirborneVelocityMessage of(RawMessage rawMessage) {

        int content = Bits.extractUInt(rawMessage.payload(), CONTENT_START, CONTENT_SIZE);
        int sType = Bits.extractUInt(rawMessage.payload(), S_TYPE_START, S_TYPE_SIZE);
        double speed, angle, speedInKnot;
        int speedSouthNorthKnot, speedEastWestKnot;
        if ((sType == FIRST_S_TYPE_FOR_GROUND_BASED_POSITION || sType == SECOND_S_TYPE_FOR_GROUND_BASED_POSITION) &&
                (speedSouthNorthKnot = Bits.extractUInt(content, SPEED_SOUTH_NORTH_START, SPEED_SOUTH_NORTH_SIZE) - 1) != INVALID_SPEED &&
                (speedEastWestKnot = Bits.extractUInt(content, SPEED_EAST_WEST_START, SPEED_EAST_WEST_SIZE) - 1) != INVALID_SPEED) {
            speedInKnot = Math.hypot(speedSouthNorthKnot, speedEastWestKnot);
            if (sType == SECOND_S_TYPE_FOR_GROUND_BASED_POSITION) speedInKnot *= 4;
            speed = Units.convertFrom(speedInKnot, Units.Speed.KNOT);
            int signSouthNorth = Bits.testBit(content, SIGN_SOUTH_NORTH_BIT_INDEX) ? -1 : 1;
            boolean directionEastWest = Bits.testBit(content, DIRECTION_EAST_WEST_BIT_INDEX);
            angle = Math.atan2(speedEastWestKnot, signSouthNorth * speedSouthNorthKnot);
            if (directionEastWest) angle = -angle + 2 * Math.PI;
            return new AirborneVelocityMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(), speed, angle);


        }
        if ((sType == FIRST_S_TYPE_FOR_AIRBORNE_POSITION || sType == SECOND_S_TYPE_FOR_AIRBORNE_POSITION) &&
                Bits.testBit(content, IS_CAP_AVAILABLE_BIT_INDEX) &&
                (speedInKnot = Bits.extractUInt(content, SPEED_IN_KNOT_START, SPEED_IN_KNOT_SIZE) - 1) != -1) {
            double angleInTurn = Bits.extractUInt(content, ANGLE_IN_TURN_START, ANGLE_IN_TURN_SIZE) / ANGLE_IN_TURN_NORMALISATION_FACTOR;
            angle = Units.convertFrom(angleInTurn, Units.Angle.TURN);
            if (sType == SECOND_S_TYPE_FOR_AIRBORNE_POSITION) speedInKnot *= 4;
            speed = Units.convertFrom(speedInKnot, Units.Speed.KNOT);
            return new AirborneVelocityMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(), speed, angle);
        }

        return null;
    }

}
