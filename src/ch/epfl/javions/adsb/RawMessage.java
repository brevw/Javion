package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.ByteString;
import ch.epfl.javions.Crc24;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.HexFormat;

import static ch.epfl.javions.Crc24.GENERATOR;

/**
 * This record represents a raw ADS-B message
 *
 * @author: Bouden Omar (341381)
 * @author: Tlili Ahmed (344939)
 */
public record RawMessage(long timeStampNs, ByteString bytes) {
    public static final int DOWN_LINK_FORMAT_SIZE = 5;
    public static final int LENGTH = 14;
    private final static Crc24 CRC_24 = new Crc24(GENERATOR);
    private static final HexFormat HF = HexFormat.of().withUpperCase();
    private static final int TYPE_CODE_START = 51, TYPE_CODE_SIZE = 5;
    private static final int DOWN_LINK_FORMAT_LOCATION_IN_BYTES = 0, DOWN_LINK_FORMAT_START = 3;    private static final int PAYLOAD_IN_BYTES_START = 4, PAYLOAD_IN_BYTES_SIZE = 7,
            PAYLOAD_IN_BYTES_FINISH_EXCLUDED = PAYLOAD_IN_BYTES_START + PAYLOAD_IN_BYTES_SIZE;
    private static final int ICAO_ADDRESS_STRING_LENGTH = 10;
    /**
     * @param timeStampNs (long) the time stamp of a message expressed in nanoseconds starting at a given origin
     * @param bytes       (ByteSting) bytes of the message
     * @throws IllegalArgumentException if the timestamp is strictly negative or the byte string does not contain LENGTH bytes
     */
    public RawMessage {
        Preconditions.checkArgument(timeStampNs >= 0 && bytes.size() == LENGTH);
    }    private static final int ICAO_ADDRESS_LOCATION_IN_BYTES_START = 1, ICAO_ADDRESS_LOCATION_IN_BYTES_SIZE = 3,
            ICAO_ADDRESS_LOCATION_IN_BYTES_FINISH = ICAO_ADDRESS_LOCATION_IN_BYTES_START + ICAO_ADDRESS_LOCATION_IN_BYTES_SIZE;

    /**
     * @param timeStampNs (long)
     * @param bytes       (bytes[])
     * @return (RawMessage) the raw ADS-B message with the given timestamp and bytes, or null if the CRC24 of the bytes is not 0
     */
    public static RawMessage of(long timeStampNs, byte[] bytes) {
        return CRC_24.crc(bytes) == 0 ? new RawMessage(timeStampNs, new ByteString(bytes)) : null;
    }

    /**
     * @param byte0 (byte) given byte, first byte of a message
     * @return (int) the size of a message whose first byte is the given one or Length if the message's type is unknown
     */
    public static int size(byte byte0) {
        return Bits.extractUInt(byte0, DOWN_LINK_FORMAT_START, DOWN_LINK_FORMAT_SIZE) == 17 ? LENGTH : 0;
    }

    /**
     * @param payload (long) given ME attribute
     * @return (int) the type code of the ME attribute passed in argument
     */

    public static int typeCode(long payload) {
        return Bits.extractUInt(payload, TYPE_CODE_START, TYPE_CODE_SIZE);
    }

    /**
     * @return (int) the message's format
     */
    public int downLinkFormat() {
        return Bits.extractUInt(bytes.byteAt(DOWN_LINK_FORMAT_LOCATION_IN_BYTES), DOWN_LINK_FORMAT_START,
                DOWN_LINK_FORMAT_SIZE);
    }

    /**
     * @return (IcaoAddress) the ICAO address of the sender of the message
     */
    public IcaoAddress icaoAddress() {
        return new IcaoAddress(HF.toHexDigits(bytes.bytesInRange(ICAO_ADDRESS_LOCATION_IN_BYTES_START, ICAO_ADDRESS_LOCATION_IN_BYTES_FINISH)).substring(ICAO_ADDRESS_STRING_LENGTH));
    }

    /**
     * @return (long) the ME attribute of the message
     */
    public long payload() {
        return bytes.bytesInRange(PAYLOAD_IN_BYTES_START, PAYLOAD_IN_BYTES_FINISH_EXCLUDED);
    }

    /**
     * @return (int) the type code of the message
     */
    public int typeCode() {
        return typeCode(payload());
    }





}
