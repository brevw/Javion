package ch.epfl.javions.demodulation;

import ch.epfl.javions.adsb.RawMessage;

import java.io.IOException;
import java.io.InputStream;


/**
 * This class represents an ADS-B messages' demodulator
 *
 * @author: Bouden Omar (341381)
 * @author: Tlili Ahmed (344939)
 */
public class AdsbDemodulator {
    private static final int SIGMA_DEFAULT_VALUE = 0;
    private static final int WINDOW_SIZE = 1200;
    private static final int TIMESTAMPS_MULTIPLICATION_FACTOR = 100;
    private final PowerWindow powerWindow;
    private long sigmaPMinusOne, sigmaP0;


    /**
     * Public AdsbDemodulator default constructor
     * Returns a demodulator obtaining the bytes containing the samples of the stream passed in argument
     *
     * @param samplesStream (InputStream) contains the bytes received from the AirSpy
     * @throws IOException if there's an output/input problem detected
     */
    public AdsbDemodulator(InputStream samplesStream) throws IOException {
        powerWindow = new PowerWindow(samplesStream, WINDOW_SIZE);
        sigmaPMinusOne = SIGMA_DEFAULT_VALUE;
        sigmaP0 = SIGMA_DEFAULT_VALUE;
    }

    /**
     * @return (RawMessage) returns the next ADS-B message of the stream
     * of samples and null if the end of the stream is reached
     * @throws IOException if there's an output/input problem detected
     */
    public RawMessage nextMessage() throws IOException {
        long sigmaV, sigmaP1;
        while (powerWindow.isFull()) {
            sigmaP1 = powerWindow.get(1) + powerWindow.get(11) + powerWindow.get(36) + powerWindow.get(46);
            sigmaV = powerWindow.get(5) + powerWindow.get(15) + powerWindow.get(20) + powerWindow.get(25) +
                    powerWindow.get(30) + powerWindow.get(40);
            if (sigmaPMinusOne < sigmaP0 && sigmaP0 > sigmaP1 && sigmaP0 >= 2 * sigmaV && getDF() == 17) {
                RawMessage rawMessage = RawMessage.of(powerWindow.position() * TIMESTAMPS_MULTIPLICATION_FACTOR, getContent());
                if (rawMessage != null) {
                    powerWindow.advanceBy(WINDOW_SIZE);
                    sigmaPMinusOne = SIGMA_DEFAULT_VALUE;
                    sigmaP0 = SIGMA_DEFAULT_VALUE;
                    return rawMessage;
                }
            }
            sigmaPMinusOne = sigmaP0;
            sigmaP0 = sigmaP1;
            powerWindow.advance();

        }
        return null;
    }

    /**
     * @return (byte[]) the bytes corresponding to the ADS-B message
     */
    private byte[] getContent() {
        int index = 0;
        byte[] bytes = new byte[RawMessage.LENGTH];
        byte temp = 0;
        for (int i = 0; i < bytes.length * Byte.SIZE; ++i) {
            byte b = (byte) (powerWindow.get(80 + 10 * i) < powerWindow.get(85 + 10 * i) ? 0 : 1);
            temp = (byte) ((temp << 1) | b);
            if (i % 8 == 7) {
                bytes[index++] = temp;
                temp = 0;
            }
        }
        return bytes;
    }

    /**
     * @return the format of the message
     */
    private int getDF() {
        int DF = 0;
        for (int i = 0; i < RawMessage.DOWN_LINK_FORMAT_SIZE; ++i) {
            byte b = (byte) (powerWindow.get(80 + 10 * i) < powerWindow.get(85 + 10 * i) ? 0 : 1);
            DF = (DF << 1) | b;
        }
        return DF;
    }
}
