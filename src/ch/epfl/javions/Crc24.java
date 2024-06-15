package ch.epfl.javions;


/**
 * CRC24 message verification class
 *
 * @author: Bouden Omar (341381)
 * @author: Tlili Ahmed (344939)
 */
public final class Crc24 {
    private static final byte CRC24_SIZE = 24;
    /**
     * crc default generator
     */
    public static final int GENERATOR = 0xFFF409;
    private static final int GENERATOR_TABLE_SIZE = 256;
    private static final int CRC24_MSB_START = CRC24_SIZE - Byte.SIZE;
    private final int[] array;

    /**
     * Public Crc24 default constructor
     * Returns a CRC24 calculator using the generator whose 24 least significant bits are those of generator
     *
     * @param generator (int)
     */
    public Crc24(int generator) {
        array = buildTable(generator);
    }

    /**
     * apply crc algorithm ( where input is treated bit by bit )
     *
     * @param bytes(byte[]) it models the input bits as an array of bytes
     * @return the 24 bit crc value as an int
     */
    private static int crc_bitwise(byte[] bytes, int generator) {
        int[] array = new int[]{0, generator};
        int crc = 0;
        for (byte b1 : bytes) {
            for (byte i = Byte.SIZE - 1; i >= 0; --i) {
                int b = b1 >> i & 0b1;
                crc = ((crc << 1) | b) ^ array[crc >> (CRC24_SIZE - 1) & 0b1];
            }
        }
        for (byte i = 0; i < CRC24_SIZE; ++i) {
            crc = (crc << 1) ^ array[crc >> (CRC24_SIZE - 1) & 0b1];
        }
        return Bits.extractUInt(crc, 0, CRC24_SIZE);
    }

    /**
     * @return an array of length 256 where each element of index i contains
     * the value of crc_bitwise done to its index
     */
    private static int[] buildTable(int generator) {
        int[] array = new int[GENERATOR_TABLE_SIZE];
        for (int i = 0; i < GENERATOR_TABLE_SIZE; ++i) {
            array[i] = crc_bitwise(new byte[]{(byte) i}, generator);
        }
        return array;
    }

    /**
     * apply crc algorithm ( where input is treated byte by byte )
     *
     * @param bytes(byte[]) array of bytes
     * @return the 24 bit crc value as an int
     */
    public int crc(byte[] bytes) {
        int crc = 0;
        for (byte b : bytes) {
            crc = ((crc << Byte.SIZE) | (b & 0xFF)) ^ array[Bits.extractUInt(crc, CRC24_MSB_START, Byte.SIZE)];
        }
        for (int i = 0; i < (CRC24_SIZE / Byte.SIZE); ++i) {
            crc = (crc << Byte.SIZE) ^ array[Bits.extractUInt(crc, CRC24_MSB_START, Byte.SIZE)];
        }
        return Bits.extractUInt(crc, 0, CRC24_SIZE);
    }
}
