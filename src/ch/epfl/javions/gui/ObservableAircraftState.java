package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.adsb.AircraftStateSetter;
import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.AircraftData;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


/**
 * The ObservableAircraftState class represents an observable aircraft state
 * that is used to update the graphical user interface of the aircraft.
 * <p>
 * This class implements the AircraftStateSetter interface, which defines
 * the methods for updating the state of the aircraft.
 * <p>
 * An instance of the ObservableAircraftState class can be used to observe
 * changes in the state of the aircraft, such as changes in its position,
 * altitude, velocity, track or heading.
 * <p>
 * An instance of the ObservableAircraftState class also keeps track of
 * the aircraft's call sign, last message timestamp and trajectory.
 * @author: Tlili Ahmed (344939)
 * @author: Bouden Omar (341381)
 */
public final class ObservableAircraftState implements AircraftStateSetter {

    private final AircraftData aircraftData;
    private final IcaoAddress icaoAddress;
    private final IntegerProperty category;
    private final LongProperty lastMessageTimeStampNs;
    private final ObjectProperty<CallSign> callSign;
    private final ObjectProperty<GeoPos> position;
    private final DoubleProperty altitude, velocity, trackOrHeading;
    private final ObservableList<AirbornePos> trajectory;
    private final ObservableList<AirbornePos> unmodifiableTrajectory;
    private long oldMessageTimeStamps;

    /**
     * The AirbornePos record contains the latitude, longitude and altitude
     * properties of a position.
     * <p>
     * It is used to record the trajectory of the aircraft.
     *
     * @param position (GeoPos)
     * @param altitude (double)
     */
    public record AirbornePos(GeoPos position, double altitude){}

    /**
     * Constructs a new ObservableAircraftState object with the specified
     * AircraftData and IcaoAddress.
     *
     * @param aircraftData The AircraftData object that contains the data
     *                     of the aircraft.
     * @param icaoAddress The IcaoAddress object that represents the unique
     *                     identifier of the aircraft.
     */
    public ObservableAircraftState(AircraftData aircraftData, IcaoAddress icaoAddress) {
        this.aircraftData = aircraftData;
        this.icaoAddress = icaoAddress;
        category = new SimpleIntegerProperty();
        lastMessageTimeStampNs = new SimpleLongProperty();
        callSign = new SimpleObjectProperty<>();
        position = new SimpleObjectProperty<>();
        altitude = new SimpleDoubleProperty(Double.NaN);
        velocity = new SimpleDoubleProperty(Double.NaN);
        trackOrHeading = new SimpleDoubleProperty();
        trajectory = FXCollections.observableArrayList();
        unmodifiableTrajectory = FXCollections.unmodifiableObservableList(trajectory);
        oldMessageTimeStamps = -1;

    }



    /**
     * @return The last message timestamp property.
     */
    public ReadOnlyLongProperty lastMessageTimeStampNsProperty() {
        return lastMessageTimeStampNs;
    }


    /**
     * @return The last message timestamp in nanoseconds.
     */
    public long getLastMessageTimeStampNs() {
        return lastMessageTimeStampNs.get();
    }


    /**
     * Sets the last message timestamp in nanoseconds.
     *
     * @param lastMessageTimeStampNs The last message timestamp in nanoseconds.
     */
    @Override
    public void setLastMessageTimeStampNs(long lastMessageTimeStampNs) {
        this.lastMessageTimeStampNs.set(lastMessageTimeStampNs);
    }


    /**
     * @return a read-only IntegerProperty representing the category of the aircraft
     */
    public ReadOnlyIntegerProperty categoryProperty(){
        return category;
    }

    /**
     * @return the int value of the categoryProperty.
     */
    public int getCategory(){return category.get(); }


    /**
     * sets the value of the categoryProperty to the specified int value.
     * @param category (int)
     */
    @Override
    public void setCategory(int category) {
        this.category.set(category);
    }


    /**
     * @return a read-only ObjectProperty<CallSign> representing the call sign of the aircraft.
     */
    public ReadOnlyObjectProperty<CallSign> callSignProperty(){
        return callSign;
    }


    /**
     * @return the CallSign object of the callSignProperty.
     */
    public CallSign getCallSign() {
        return callSign.get();
    }


    /**
     * sets the value of the callSignProperty to the specified CallSign object.
     * @param callSign (CallSign)
     */
    @Override
    public void setCallSign(CallSign callSign) {
        this.callSign.set(callSign);
    }

    /**
     * @return a read-only ObjectProperty<GeoPos> representing the position of the aircraft.
     */
    public ReadOnlyObjectProperty<GeoPos> positionProperty() {
        return position;
    }

    /**
     * @return the current GeoPos object representing the position of the aircraft.
     */
    public GeoPos getPosition() {
        return position.get();
    }

    /**
     * sets the value of the positionProperty to the specified GeoPos object.
     * @param position (GeoPos)
     */
    @Override
    public void setPosition(GeoPos position) {
        if(!Double.isNaN(getAltitude()))
            trajectory.add( new AirbornePos(position, getAltitude()) );
        oldMessageTimeStamps = getLastMessageTimeStampNs();
        this.position.set(position);
    }

    /**
     * @return a read-only DoubleProperty representing the altitude of the aircraft.
     */
    public ReadOnlyDoubleProperty altitudeProperty() {
        return altitude;
    }

    /**
     * @return the current altitude of the aircraft.
     */
    public double getAltitude() {
        return altitude.get();
    }

    /**
     * sets the value of the altitudeProperty to the specified double value.
     * @param altitude (double)
     */
    @Override
    public void setAltitude(double altitude) {
        if(getPosition()!=null){
            if(trajectory.isEmpty()) trajectory.add( new AirbornePos(getPosition(), altitude) );
            else if(getLastMessageTimeStampNs() == oldMessageTimeStamps)
                trajectory.set(trajectory.size()-1, new AirbornePos(getPosition(), altitude));
        }
        oldMessageTimeStamps = getLastMessageTimeStampNs();
        this.altitude.set(altitude);
    }


    /**
     * @return a read-only DoubleProperty representing the velocity of the aircraft.
     */
    public ReadOnlyDoubleProperty velocityProperty() {
        return velocity;
    }


    /**
     * @return the current velocity of the aircraft.
     */
    public double getVelocity() {
        return velocity.get();
    }


    /**
     * sets the value of the velocityProperty to the specified double value.
     * @param velocity (double)
     */
    @Override
    public void setVelocity(double velocity) {
        this.velocity.set(velocity);
    }


    /**
     * @return the current track or heading of the aircraft.
     */
    public ReadOnlyDoubleProperty trackOrHeadingProperty() {
        return trackOrHeading;
    }

    /**
     * @return a read-only DoubleProperty representing the track or heading of the aircraft.
     */
    public double getTrackOrHeading() {
        return trackOrHeading.get();
    }


    /**
     * sets the value of the trackOrHeadingProperty to the specified double value.
     * @param trackOrHeading (double)
     */
    @Override
    public void setTrackOrHeading(double trackOrHeading) {
        this.trackOrHeading.set(trackOrHeading);
    }


    /**
     * @return an unmodifiable ObservableList<AirbornePos> representing the trajectory of the aircraft.
     */
    public ObservableList<AirbornePos> getTrajectory(){
        return unmodifiableTrajectory;
    }

    /**
     * @return icaoAddress (IcaoAddress) related to the aircraft
     */
    public IcaoAddress address() {
        return icaoAddress;
    }

    /**
     * @return (AircraftData) containing aircraft data registration, model, etc..
     */
    public AircraftData getAircraftData() {
        return aircraftData;
    }

}
