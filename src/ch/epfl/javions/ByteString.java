package ch.epfl.javions;


import java.util.Arrays;
import java.util.HexFormat;
import java.util.Objects;

/**
 * This class represents a sequence(string) of bytes.
 *
 * @author: Bouden Omar (341381)
 * @author: Tlili Ahmed (344939)
 */

public final class ByteString {
    private static final HexFormat hf = HexFormat.of().withUpperCase();
    private final byte[] bytes;

    /**
     * ByteString public constructor
     * Returns a string of bytes whose content is that of the array passed as argument
     *
     * @param bytes (bytes[]) The given byte static array to be cloned and used in this class
     */
    public ByteString(byte[] bytes) {
        this.bytes = bytes.clone();
    }

    /**
     * @param hexString (String) hexadecimal representation of a byte string
     * @return (ByteString) the byte string after conversion from the hexadecimal base
     * @throws IllegalArgumentException if the given string is cannot be a hexadecimal
     *                                  representation of a byte string (meaning that the given string is not of even length,
     *                                  or  contains a character which is not a hexadecimal digit)
     */
    public static ByteString ofHexadecimalString(String hexString) {
        Preconditions.checkArgument(!Bits.testBit(hexString.length(), 0));
        return new ByteString(hf.parseHex(hexString));
    }

    /**
     * @return (int) the string f bytes' length
     */
    public int size() {
        return bytes.length;
    }

    /**
     * Extracts the byte at the index position of the sequence
     *
     * @param index (int) position of the byte we want to extract in the sequence
     * @return (int) the byte at the index position of the sequence as an integer
     */
    public int byteAt(int index) {
        Objects.checkIndex(index, size());
        return Byte.toUnsignedInt(bytes[index]);
    }

    /**
     * Extracts the subsequence of bytes between fromIndex (included) amd toIndex
     * (excluded) from the bytes' sequence
     *
     * @param fromIndex (int)
     * @param toIndex   (int)
     * @return the byte subsequence included between fromIndex (included) amd toIndex
     * (excluded) from the bytes' sequence
     * @throws IndexOutOfBoundsException if the range given by fromIndex and toIndex
     *                                   is not totally included between 0 and the string's size
     * @throws IllegalArgumentException  if the difference between toIndex and fromIndex
     *                                   is not strictly less than the number of bytes in a long value
     */
    public long bytesInRange(int fromIndex, int toIndex) {
        Objects.checkFromToIndex(fromIndex, toIndex, size());
        Preconditions.checkArgument(toIndex - fromIndex <= Long.SIZE);
        long toReturn = 0L;
        for (int i = fromIndex; i < toIndex; ++i) {
            toReturn = toReturn | ((long) byteAt(i) << ((toIndex - i - 1) * (Long.SIZE / Byte.SIZE)));
        }
        return toReturn;
    }

    @Override
    public boolean equals(Object other0) {
        return (other0 instanceof ByteString other) && Arrays.equals(other.bytes, this.bytes);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(bytes);
    }

    @Override
    public String toString() {
        return hf.formatHex(bytes);
    }

}
