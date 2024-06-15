/**
 * This interface contains abstract setter methods for different attributes of an aircraft's state
 * and is intended to be implemented by all classes representing the state of an aircraft
 *
 * @author: Bouden Omar (341381)
 * @author: Tlili Ahmed (344939)
 */

package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;

public interface AircraftStateSetter {
    /**
     * Sets the timestamp of the last received message from the aircraft to the given value
     *
     * @param timeStampNs (long)
     */
    void setLastMessageTimeStampNs(long timeStampNs);

    /**
     * Sets the aircraft's category to the given value
     *
     * @param category (int)
     */
    void setCategory(int category);

    /**
     * Sets the call sign of the aircraft to the given value
     *
     * @param callSign (CallSign)
     */
    void setCallSign(CallSign callSign);

    /**
     * Sets the aircraft's position to the given value
     *
     * @param position (GeoPos)
     */
    void setPosition(GeoPos position);

    /**
     * Sets the aircraft's altitude to the given value
     *
     * @param altitude (double)
     */
    void setAltitude(double altitude);

    /**
     * Sets the aircraft's speed to the given value
     *
     * @param velocity (double)
     */
    void setVelocity(double velocity);

    /**
     * Sets the direction of the aircraft to the given value
     *
     * @param trackOrHeading (double)
     */
    void setTrackOrHeading(double trackOrHeading);
}
