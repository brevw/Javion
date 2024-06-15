package ch.epfl.javions.gui;

import ch.epfl.javions.adsb.AircraftStateAccumulator;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.aircraft.AircraftData;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents a managing system responsible for the creation, storing, and updating of aircraft states.
 * It maintains a map of all known aircraft states, where the key is the ICAO address of the aircraft, and the value is
 * an AircraftStateAccumulator instance, which accumulates messages to create an ObservableAircraftState instance.
 * <p>
 * This class also has a purge method that removes any state with last properties dating more than a minute from the current
 * message time.
 *
 * @author: Tlili Ahmed (344939)
 * @author: Bouden Omar (341381)
 */
public final class AircraftStateManager {
    private static final long MINUTE_IN_NANOSECONDE = 60_000_000_000L;
    private final AircraftDatabase aircraftDatabase;
    private final Map<IcaoAddress, AircraftStateAccumulator<ObservableAircraftState>> aircraftStateMap;
    private final ObservableSet<ObservableAircraftState> observableAircraftStates;
    private final ObservableSet<ObservableAircraftState> unmodifiableObservableAircraftStates;
    private long lastTimeStamp;

    /**
     * The constructor initializes an instance of the class with an aircraft database, which is used to retrieve
     * information about the aircraft such as their type designator, registration, and description. It also creates
     * an empty hashmap to store the aircraft state accumulators, an observable set to store the observable aircraft states,
     * and an unmodifiable version of the observable set.
     @param aircraftDatabase (AircraftDatabase) The database to retrieve aircraft information from.
     */
    public AircraftStateManager(AircraftDatabase aircraftDatabase) {
        this.aircraftDatabase = aircraftDatabase;
        this.aircraftStateMap = new HashMap<>();
        this.observableAircraftStates = FXCollections.observableSet();
        this.unmodifiableObservableAircraftStates = FXCollections.unmodifiableObservableSet(observableAircraftStates);
        lastTimeStamp = 0;
    }

    /**
     * @return An unmodifiable observable set of all current aircraft states.
     */
    public ObservableSet<ObservableAircraftState> states() {
        return unmodifiableObservableAircraftStates;
    }


    /**
     * Updates the aircraftStateMap and observableAircraftStates with a given message.
     * <p>
     * If the ICAO address of the message is not in the aircraftStateMap, a new AircraftStateAccumulator instance is
     * created, and the message is passed to it. If the AircraftStateAccumulator instance does not yet have a position
     * set, then the observableAircraftStates set is updated with the new ObservableAircraftState.
     @param message The message to update the state with.
     */
    public void updateWithMessage(Message message) throws IOException {
        lastTimeStamp = message.timeStampNs();
        IcaoAddress icaoAddress = message.icaoAddress();
        if(!aircraftStateMap.containsKey(icaoAddress)){
            AircraftData aircraftData = aircraftDatabase.get(icaoAddress);
            ObservableAircraftState observableAircraftState = new ObservableAircraftState(aircraftData, icaoAddress);
            aircraftStateMap.put(icaoAddress, new AircraftStateAccumulator<>(observableAircraftState));
        }
        aircraftStateMap.get(icaoAddress).update(message);
        if((aircraftStateMap.get(icaoAddress).stateSetter()).getPosition() != null)
            observableAircraftStates.add(aircraftStateMap.get(icaoAddress).stateSetter());

    }

    /**
     * Removes any state with last properties dating more than a minute from the current message time.
     */
    public void purge(){
        observableAircraftStates.removeIf(state -> {

            boolean shouldRemove = (lastTimeStamp - state.getLastMessageTimeStampNs()) > MINUTE_IN_NANOSECONDE;
            if (shouldRemove){
                aircraftStateMap.remove(state.address());
                return true;
            }
            return false;

        });
    }
}
