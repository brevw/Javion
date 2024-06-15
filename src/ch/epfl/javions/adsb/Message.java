package ch.epfl.javions.adsb;

import ch.epfl.javions.aircraft.IcaoAddress;

/**
 * This interface contains abstract methods for getting timestamp and Icao address and is meant
 * to be implemented by all classes representing an analysed ADS-B message
 *
 * @author: Bouden Omar (341381)
 * @author: Tlili Ahmed (344939)
 */
public interface Message {
    /**
     * @return (long) the message's time stamp in nanoseconds
     */
    long timeStampNs();

    /**
     * @return (IcaoAddress) the ICAO address of the message's sender
     */
    IcaoAddress icaoAddress();
}
