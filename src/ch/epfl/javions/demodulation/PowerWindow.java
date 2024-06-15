package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * This class represents a fixed-sized window over a sequence
 * of power samples produced by a power computer
 *
 * @author: Bouden Omar (341381)
 * @author: Tlili Ahmed (344939)
 */
public final class PowerWindow {
    private final int BATCH_SIZE = 1 << 16;
    private final int TWO_BATCHES_SIZE = 1 << 17;
    private final int windowSize;
    private final int[] container1, container2;
    private final PowerComputer powerComputer;
    private int lastElementPos;
    private long position;
    private int lastDecodedSampleIndex;

    /**
     * public PowerComputer default constructor
     * Returns a window of given size on the sequence of power samples computed from the bytes
     * provided by the given input stream
     *
     * @param stream     (InputStream) contains the bytes received from the AirSpy
     * @param windowSize (int)
     * @throws IllegalArgumentException if the given window size is not a strictly positive
     *                                  integer smaller or equal to 2^16
     * @throws IOException              if there's an output/input problem detected
     */
    public PowerWindow(InputStream stream, int windowSize) throws IOException {
        Preconditions.checkArgument(windowSize > 0 && windowSize <= BATCH_SIZE);
        powerComputer = new PowerComputer(stream, BATCH_SIZE);
        this.windowSize = windowSize;
        container1 = new int[BATCH_SIZE];
        container2 = new int[BATCH_SIZE];
        lastDecodedSampleIndex = powerComputer.readBatch(container1) - 1;
        lastElementPos = windowSize - 1;
    }

    /**
     * @return (int) the size of the window
     */
    public int size() {
        return windowSize;
    }

    /**
     * @return (long) the current window position with respect
     * to the beginning of the power samples' stream which is initially 0
     */
    public long position() {
        return position;
    }

    /**
     * @return true iff the window if full and false otherwise
     */
    public boolean isFull() {
        if (lastDecodedSampleIndex == BATCH_SIZE - 1) return true;
        else return lastDecodedSampleIndex >= lastElementPos && (lastDecodedSampleIndex != 0 || lastElementPos != 0);
    }

    /**
     * @param i (int)
     * @return the power sample at the given index of the window
     * @throws IndexOutOfBoundsException if the given index is not
     *                                   a positive integer(can be zero) strictly smaller than the window size
     */
    public int get(int i) {
        Objects.checkIndex(i, windowSize);
        int index = lastElementPos - windowSize + 1 + i;
        if (index < 0) index += TWO_BATCHES_SIZE;

        if (index < BATCH_SIZE) return container1[index];
        else return container2[index - BATCH_SIZE];
    }

    /**
     * advances the window by one sample
     *
     * @throws IOException if there's an output/input problem detected
     */
    public void advance() throws IOException {
        lastElementPos = ++lastElementPos == TWO_BATCHES_SIZE ? 0 : lastElementPos;
        ++position;

        if (lastElementPos == 0) lastDecodedSampleIndex = powerComputer.readBatch(container1) - 1;
        if (lastElementPos == BATCH_SIZE) lastDecodedSampleIndex = powerComputer.readBatch(container2) - 1;
    }


    /**
     * advances the window by the given number "offset" of samples
     *
     * @param offset (int) given number of samples by which the window needs to be advanced
     * @throws IllegalArgumentException if offset is not positive or equals to zero
     * @throws IOException              if there's an output/input problem detected
     */
    public void advanceBy(int offset) throws IOException {
        Preconditions.checkArgument(offset > 0);
        for (int i = 0; i < offset; ++i) {
            advance();
        }
    }

}
