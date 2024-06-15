/**
 * This class represents an object that accumulates ADS-B messages from
 * a single aircraft to determine its state over time
 *
 * @author: Bouden Omar (341381)
 * @author: Tlili Ahmed (344939)
 */
package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;

import java.util.Objects;

public final class AircraftStateAccumulator<T extends AircraftStateSetter> {
    private static final long DELTA_TIME_LIMIT = 10_000_000_000L;
    private final T stateSetter;
    private AirbornePositionMessage even, odd;

    /**
     * Public AircraftStateAccumulator default constructor
     * Returns an aircraft state accumulator associated with the given modifiable state
     *
     * @param stateSetter (T)
     */
    public AircraftStateAccumulator(T stateSetter) {
        this.stateSetter = Objects.requireNonNull(stateSetter);
        even = null;
        odd = null;
    }

    /**
     * @return (T) the modifiable state of the aircraft
     */
    public T stateSetter() {
        return stateSetter;
    }


    /**
     * Updates the modifiable state of the aircraft according to the given message
     *
     * @param message (Message) the received message
     */

    public void update(Message message) {
        stateSetter.setLastMessageTimeStampNs(message.timeStampNs());
        switch (message) {
            case AircraftIdentificationMessage aim -> {
                stateSetter.setCategory(aim.category());
                stateSetter.setCallSign(aim.callSign());
            }
            case AirbornePositionMessage apm -> {
                stateSetter.setAltitude(apm.altitude());
                if (apm.parity() == 0) even = apm;
                else odd = apm;
                if (even != null && odd != null && Math.abs(even.timeStampNs() - odd.timeStampNs()) < DELTA_TIME_LIMIT) {
                    GeoPos position = CprDecoder.decodePosition(even.x(), even.y(), odd.x(), odd.y(), apm.parity());
                    if (position != null) stateSetter.setPosition(position);
                }
            }
            case AirborneVelocityMessage avm -> {
                stateSetter.setVelocity(avm.speed());
                stateSetter.setTrackOrHeading(avm.trackOrHeading());
            }
            default -> throw new IllegalArgumentException();
        }
    }

}
