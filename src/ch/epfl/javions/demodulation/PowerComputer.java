package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;
import java.io.IOException;
import java.io.InputStream;

/**
 * This class represents an object for calculating the signed samples already
 * transformed by SamplesDecoder
 *
 * @author: Bouden Omar (341381)
 * @author: Tlili Ahmed (344939)
 */
public final class PowerComputer {
    private static final int OLD_SAMPLES_LENGTH = 8;
    private final short[] samples;
    private final SamplesDecoder samplesDecoder;
    private final short[] oldSamples;
    private int tailPos;

    /**
     * public PowerComputer default constructor
     *
     * @param stream    (Input stream) contains the bytes received from the AirSpy
     * @param batchSize (int) samples' batch size
     * @throws IllegalArgumentException if the given batch size is not
     *                                  valid (valid meaning that it must be a positive integer, 0 included)
     */
    public PowerComputer(InputStream stream, int batchSize) {
        Preconditions.checkArgument(batchSize % Byte.SIZE == 0 && batchSize > 0);
        samples = new short[2 * batchSize];
        samplesDecoder = new SamplesDecoder(stream, 2 * batchSize);
        oldSamples = new short[OLD_SAMPLES_LENGTH];
        tailPos = OLD_SAMPLES_LENGTH - 1;
    }

    /**
     * Reads from the samples decoder the needed number of samples for calculating a batch's
     * power samples, then does these calculations.
     *
     * @param batch (int[]) array to be filled with the power samples
     * @return the number of power samples placed in the array "batch"
     * @throws IOException              if there's an output/input problem detected
     * @throws IllegalArgumentException if the table in argument does not have the same length as a batch
     */
    public int readBatch(int[] batch) throws IOException {
        Preconditions.checkArgument(samples.length / 2 == batch.length);
        int index = 0;
        int count = samplesDecoder.readBatch(samples);
        for (int i = 0; i < count; i += 2) {
            tailPos = ++tailPos >= OLD_SAMPLES_LENGTH ? tailPos - OLD_SAMPLES_LENGTH : tailPos;
            oldSamples[tailPos] = samples[i];

            tailPos = ++tailPos >= OLD_SAMPLES_LENGTH ? tailPos - OLD_SAMPLES_LENGTH : tailPos;
            oldSamples[tailPos] = samples[i + 1];

            int p1 = oldSamples[0] - oldSamples[2] + oldSamples[4] - oldSamples[6];
            int p2 = oldSamples[1] - oldSamples[3] + oldSamples[5] - oldSamples[7];
            batch[index++] = p1 * p1 + p2 * p2;
        }
        return count / 2;
    }

}
