package ch.epfl.javions.adsb;



/**
 * This class transforms the raw ADS-B messages into one of the defined three types of messages
 *
 * @author: Bouden Omar (341381)
 * @author: Tlili Ahmed (344939)
 */
public class MessageParser {

    private MessageParser() {
    }

    /**
     * @param rawMessage (RawMessage)
     * @return The instance of AircraftIdentificationMessage, AirbornePositionMessage
     * or AirborneVelocityMessage corresponding to the given raw message, or null if the message's
     * type code does not correspond to any of these three message types, or if it is invalid
     */
    public static Message parse(RawMessage rawMessage) {
        int typeCode = rawMessage.typeCode();

        switch (typeCode) {
            case 19 -> {
                return AirborneVelocityMessage.of(rawMessage);
            }
            case 1, 2, 3, 4 -> {
                return AircraftIdentificationMessage.of(rawMessage);
            }
            case 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 20, 21, 22 -> {
                return AirbornePositionMessage.of(rawMessage);
            }
            default -> {
                return null;
            }
        }
    }
}
