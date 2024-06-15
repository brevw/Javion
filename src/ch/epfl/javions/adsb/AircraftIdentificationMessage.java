package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.Objects;


/**
 * This record represents an  ADS-B message of identification and category
 *
 * @author: Bouden Omar (341381)
 * @author: Tlili Ahmed (344939)
 */
public record AircraftIdentificationMessage(long timeStampNs, IcaoAddress icaoAddress, int category,
                                            CallSign callSign) implements Message {

    private static final int ENCODED_AS_UPPERCASE_ALPHABET_LOWER_BOUND = 1, ENCODED_AS_UPPERCASE_ALPHABET_UPPER_BOUND = 26,
            OFFSET_FOR_UPPERCASE_ALPHABET = 64, ENCODED_CHARACTER_AS_INT_SIZE = 6, ENCODED_CHARACTER_AS_INT_START = 42,
            ENCODED_CHARACTER_AS_INT_FINISH = 0;
    private static final int ENCODED_AS_SPACE = 32, ENCODED_AS_DIGIT_LOWER_BOUND = 48, ENCODED_AS_DIGIT_UPPER_BOUND = 57;
    private static final int CATEGORY_SECOND_PART_START = 48, CATEGORY_SECOND_PART_SIZE = 3, SPECIAL_TYPE_CODE_CONSTANT = 14,
            CATEGORY_FIRST_PART_STARTING_INDEX = CATEGORY_SECOND_PART_SIZE + 1;


    /**
     * Public AircraftIdentificationMessage compact constructor
     *
     * @param timeStampNs (long) The time stamp of the message, expressed in nanoseconds
     * @param icaoAddress (IcaoAddress) The ICAO address of the message's sender
     * @param category    (int) The sender's aircraft category
     * @param callSign    (CallSign) The sender's call sign
     * @throws NullPointerException     if icaoAddress pr callSign is null
     * @throws IllegalArgumentException if timeStampNs is strictly lower than 0
     */
    public AircraftIdentificationMessage {
        Objects.requireNonNull(icaoAddress);
        Objects.requireNonNull(callSign);
        Preconditions.checkArgument(timeStampNs >= 0);
    }


    /**
     * @param rawMessage (RawMessage)
     * @return (AircraftIdentificationMessage) the identification message corresponding to the
     * given raw message, or null if at least one of its call sign's characters is invalid
     */
    public static AircraftIdentificationMessage of(RawMessage rawMessage) {
        long payload = rawMessage.payload();
        StringBuilder b = new StringBuilder();
        int encodedCharacterAsInt;
        char decodedChar;

        for (int i = ENCODED_CHARACTER_AS_INT_START; i >= ENCODED_CHARACTER_AS_INT_FINISH; i -= ENCODED_CHARACTER_AS_INT_SIZE) {
            encodedCharacterAsInt = Bits.extractUInt(payload, i, ENCODED_CHARACTER_AS_INT_SIZE);
            if (ENCODED_AS_UPPERCASE_ALPHABET_LOWER_BOUND <= encodedCharacterAsInt && encodedCharacterAsInt <= ENCODED_AS_UPPERCASE_ALPHABET_UPPER_BOUND)
                decodedChar = (char) (OFFSET_FOR_UPPERCASE_ALPHABET + encodedCharacterAsInt);
            else if (ENCODED_AS_DIGIT_LOWER_BOUND <= encodedCharacterAsInt && encodedCharacterAsInt <= ENCODED_AS_DIGIT_UPPER_BOUND)
                decodedChar = (char) (encodedCharacterAsInt);
            else if (encodedCharacterAsInt == ENCODED_AS_SPACE) decodedChar = ' ';
            else return null;
            b.append(decodedChar);
        }

        int category = ((SPECIAL_TYPE_CODE_CONSTANT - rawMessage.typeCode()) << CATEGORY_FIRST_PART_STARTING_INDEX) |
                Bits.extractUInt(payload, CATEGORY_SECOND_PART_START, CATEGORY_SECOND_PART_SIZE);

        CallSign callSign = new CallSign(b.toString().stripTrailing());
        return new AircraftIdentificationMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(), category, callSign);

    }


}
