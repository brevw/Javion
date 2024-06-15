package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * This class represents an object capable of transforming
 * bytes, received from the AirSpy, in samples of 12 signed bits
 *
 * @author: Bouden Omar (341381)
 * @author: Tlili Ahmed (344939)
 */
public final class SamplesDecoder {
    private static final int CENTERING_OFFSET = 2048;
    private final InputStream inputStream;
    private final byte[] bytes;

    /**
     * Samples Decoder's public constructor
     *
     * @param stream    (InputStream) contains the bytes received from the AirSpy
     * @param batchSize (int) samples' batch size
     */
    public SamplesDecoder(InputStream stream, int batchSize) {
        Preconditions.checkArgument(batchSize > 0);
        inputStream = Objects.requireNonNull(stream);
        bytes = new byte[Short.BYTES * batchSize];
    }

    /**
     * Reads "batchSize" bites from the stream passed to the constructor, them converts
     * the read bytes to signed samples
     * Returns a sample decoder using the given input stream to get the bytes from the
     * AirSpy radio and producing the samples in batches of given size
     *
     * @param batch (short[]) array containing the converted signed samples
     * @return (int) the number of converted samples
     * @throws IllegalArgumentException if the table in argument does
     *                                  not have the same length as a batch
     * @throws IOException              if there's an output/input problem detected
     */

    public int readBatch(short[] batch) throws IOException {
        Preconditions.checkArgument(bytes.length / 2 == batch.length);

        int count = inputStream.readNBytes(bytes, 0, bytes.length);

        for (int i = 0; i < count / 2; ++i) {
            short b0 = (short) (bytes[2 * i] & 0xFF);
            short b1 = (short) (bytes[2 * i + 1] & 0xFF);
            short sample = (short) ((b0 | (b1 << Byte.SIZE)) - CENTERING_OFFSET);
            batch[i] = sample;
        }
        return count / 2;

    }

}
