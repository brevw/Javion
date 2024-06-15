package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.Objects;

/**
 * This record represents an ADS-B flight positioning message
 *
 * @author: Bouden Omar (341381)
 * @author: Tlili Ahmed (344939)
 */

public record AirbornePositionMessage(long timeStampNs, IcaoAddress icaoAddress, double altitude, int parity, double x,
                                      double y) implements Message {
    private static final int ALT_DATA_CONTAINER_START = 36, ALT_DATA_CONTAINER_SIZE = 12;
    private static final int Q_INDEX = 4;
    private static final int REFERENCE_ALTITUDE_WHEN_Q = -1000, REFERENCE_ALTITUDE_WHEN_NOT_Q = -1300;
    private static final int PARITY_INDEX = 34;
    private static final int LONGITUDE_START = 0, LATITUDE_START = 17, LONGITUDE_AND_LATITUDE_SIZE = 17;
    private static final int MULTIPLES_OF_HUNDRED_FEET_START = 0, MULTIPLES_OF_HUNDRED_FEET_SIZE = 3;
    private static final int MULTIPLES_OF_FIVE_HUNDRED_FEET_START = 3, MULTIPLES_OF_FIVE_HUNDRED_FEET_SIZE = 9;
    private static final int FIRST_INVALID_MULTIPLES_OF_HUNDRED_FEET = 0, SECOND_INVALID_MULTIPLES_OF_HUNDRED_FEET = 5,
            THIRD_INVALID_MULTIPLES_OF_HUNDRED_FEET = 6, FOURTH_INVALID_MULTIPLES_OF_HUNDRED_FEET = 7;
    private static final int[] NEW_ORDER_OF_PERMUTATIONS = new int[]{4, 2, 0, 10, 8, 6, 5, 3, 1, 11, 9, 7};
    private static final int NORMALISATION_FACTOR = 131072;
    private static final int MULTIPLES_OF_TWENTY_FIVE_LHS_START = 5, MULTIPLES_OF_TWENTY_FIVE_LHS_SIZE = 7,
            MULTIPLES_OF_TWENTY_FIVE_RHS_START = 0, MULTIPLES_OF_TWENTY_FIVE_RHS_SIZE = 4;

    /**
     * Public AirbornePositionMessage compact constructor
     *
     * @param timeStampNs (long) the time stamp of the message expressed in nanoseconds
     * @param icaoAddress (IcaoAddress) the ICAO address of the message's sender
     * @param altitude    (double) the aircraft's altitude at the time of sending the message
     * @param parity      (int) 0 if it is even, 1 if it is odd
     * @param x           (double) the local and normalized longitude of the aircraft at the time of sending the message
     * @param y           (double) the local and normalized latitude of the aircraft at the time of sending the message
     * @throws NullPointerException     if icaoAddress is null
     * @throws IllegalArgumentException if timeStamp is strictly less than 0, or parity
     *                                  is different from 0 or 1, or x or y are not between 0 (included) and 1 (excluded)
     */
    public AirbornePositionMessage {
        Objects.requireNonNull(icaoAddress);
        Preconditions.checkArgument(timeStampNs >= 0 && (parity == 0 || parity == 1) && x >= 0 && x < 1 && y >= 0 && y < 1);
    }

    /**
     * @param alt (int)
     * @return alt after having swapped its bits in the specific described way
     */
    private static int reorderBits(int alt) {
        int reorderedAlt = 0;
        for (int i : NEW_ORDER_OF_PERMUTATIONS)
            reorderedAlt = (reorderedAlt << 1) | (Bits.testBit(alt, i) ? 1 : 0);
        return reorderedAlt;
    }


    /**
     * @param toDecode (int) interpreted in Gray code
     * @param size     (int) number of bits to decode
     * @return result of the Gray decoding of the toDecode integer
     */
    private static int decodeGray(int toDecode, int size) {
        int decoded = 0;
        for (int i = 0; i < size; ++i)
            decoded = decoded ^ (toDecode >> i);
        return decoded;
    }

    /**
     * @param rawMessage (RawMessage)
     * @return (AirbornePositionMessage) the given airborne in-flight positioning raw message, or null if the altitude contained in the message is invalid
     */

    public static AirbornePositionMessage of(RawMessage rawMessage) {
        double alt, altInFoot;
        long payload = rawMessage.payload();
        int altDataContainer = Bits.extractUInt(payload, ALT_DATA_CONTAINER_START, ALT_DATA_CONTAINER_SIZE);
        boolean Q = Bits.testBit(altDataContainer, Q_INDEX);
        if (Q) {
            int multiplesOfTwentyFive = (Bits.extractUInt(altDataContainer, MULTIPLES_OF_TWENTY_FIVE_LHS_START, MULTIPLES_OF_TWENTY_FIVE_LHS_SIZE) << MULTIPLES_OF_TWENTY_FIVE_RHS_SIZE) |
                    Bits.extractUInt(altDataContainer, MULTIPLES_OF_TWENTY_FIVE_RHS_START, MULTIPLES_OF_TWENTY_FIVE_RHS_SIZE);
            alt = Units.convertFrom(multiplesOfTwentyFive * 25 + REFERENCE_ALTITUDE_WHEN_Q, Units.Length.FOOT);
        } else {
            int reorderedAltDataContainer = reorderBits(altDataContainer);
            int multiplesOfHundredFeet = decodeGray(Bits.extractUInt(reorderedAltDataContainer, MULTIPLES_OF_HUNDRED_FEET_START,
                    MULTIPLES_OF_HUNDRED_FEET_SIZE), MULTIPLES_OF_HUNDRED_FEET_SIZE);
            int multiplesOfFiveHundredFeet = decodeGray(Bits.extractUInt(reorderedAltDataContainer, MULTIPLES_OF_FIVE_HUNDRED_FEET_START,
                    MULTIPLES_OF_FIVE_HUNDRED_FEET_SIZE), MULTIPLES_OF_FIVE_HUNDRED_FEET_SIZE);
            switch (multiplesOfHundredFeet) {
                case FIRST_INVALID_MULTIPLES_OF_HUNDRED_FEET, SECOND_INVALID_MULTIPLES_OF_HUNDRED_FEET,
                        THIRD_INVALID_MULTIPLES_OF_HUNDRED_FEET -> {
                    return null;
                }
                case FOURTH_INVALID_MULTIPLES_OF_HUNDRED_FEET -> multiplesOfHundredFeet = 5;
            }
            if (Bits.testBit(multiplesOfFiveHundredFeet, 0)) multiplesOfHundredFeet = 6 - multiplesOfHundredFeet;


            altInFoot = REFERENCE_ALTITUDE_WHEN_NOT_Q + multiplesOfHundredFeet * 100 + multiplesOfFiveHundredFeet * 500;
            alt = Units.convertFrom(altInFoot, Units.Length.FOOT);
        }
        int parity = Bits.testBit(payload, PARITY_INDEX) ? 1 : 0;
        double longitude = (double) Bits.extractUInt(payload, LONGITUDE_START, LONGITUDE_AND_LATITUDE_SIZE) / NORMALISATION_FACTOR;
        double latitude = (double) Bits.extractUInt(payload, LATITUDE_START, LONGITUDE_AND_LATITUDE_SIZE) / NORMALISATION_FACTOR;

        return new AirbornePositionMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(), alt, parity, longitude, latitude);


    }

}
