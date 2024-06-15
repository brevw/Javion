package ch.epfl.javions;

import java.util.Objects;

/**
 * This class defines static methods for extracting a sequence of bits from a type long value.
 *
 * @author: Bouden Omar (341381)
 * @author: Tlili Ahmed (344939)
 */
public class Bits {
    private Bits() {
    }

    /**
     * Extracts range of bits of length "size" starting at index "start" from the type long "value"
     *
     * @param value (long) value to extract the bits from
     * @param start (int) index starting at which we start the extraction
     * @param size  (int) size of the sequence to be extracted
     * @return (int) the extracted sequence
     * @throws IllegalArgumentException  if the size is not strictly greater than 0 and strictly less than 32
     * @throws IndexOutOfBoundsException if the range defined by start and size is not included
     *                                   between 0 (included) and 64 (excluded)
     */
    public static int extractUInt(long value, int start, int size) {
        Preconditions.checkArgument(0 < size && size < Integer.SIZE);
        Objects.checkFromIndexSize(start, size, Long.SIZE);
        int shiftedRight = (int) (value >> start);
        int mask = (1 << size) - 1;
        return shiftedRight & mask;
    }

    /**
     * test the bit located at "index" of the long "value" and return true iif it is 1
     *
     * @param value (long)
     * @param index (int) position of the bit we're testing in value
     * @return (boolean) true if the bit  of "value" in the "index" position
     * is equal to 1 and false otherwise
     * @throws IndexOutOfBoundsException if index is not in the range (0, 64(
     */
    public static boolean testBit(long value, int index) {
        Objects.checkIndex(index, Long.SIZE);
        return ((value >> index) & 1) == 1;
    }
}
