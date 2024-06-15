package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Units;
import ch.epfl.javions.WebMercator;
import ch.epfl.javions.aircraft.*;
import ch.epfl.javions.gui.ObservableAircraftState.AirbornePos;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;

import java.util.List;

import static javafx.scene.paint.CycleMethod.NO_CYCLE;

/**
 * The `AircraftController` class controls the aircraft graphics and their properties on the map.
 * <p>
 * It is responsible for drawing the planes, labels showing their velocity, altitude, etc., and the trajectory of a plane when it is selected.
 * <p>
 *  The color of the plane and trajectory depends on its current altitude.
 * <p>
 * The graphics update based on exterior changes coming from the `MapParameters`.
 * <p>
 * This class contains a `Pane` that holds all the aircraft-related graphics.
 * <p>
 * It listens to changes in the `ObservableSet<ObservableAircraftState>` that contains all the `ObservableAircraftState` objects representing aircraft on the map, and updates the `Pane` accordingly.
 * <p>
 * An `ObjectProperty<ObservableAircraftState>` is used to store the state of the active plane, i.e., the plane whose trajectory is currently drawn on the map. The graphics for the active plane are shown along with its label, while the graphics for all other planes are hidden.
 * <p>
 * Aircraft icons are represented by SVGPaths that are bound to the corresponding `ObservableAircraftState` objects. Labels showing the altitude, velocity, and call sign of the aircraft are represented by `Rectangle`s and `Text` objects.
 * <p>
 * The altitude of the aircraft is used to determine the color of the aircraft icon and its trajectory on the map. A predefined color ramp named "plasma" is used to map altitudes to colors.
 * <p>
 * This class also handles the construction of trajectory lines on the map. Trajectory lines are visible when a plane is selected, and they are constructed by joining the consecutive `AirbornePos` objects in the plane's trajectory list using `Line` objects. The color of each line segment depends on the altitudes of the `AirbornePos` objects at its endpoints.
 *
 * @author: Tlili Ahmed (344939)
 * @author: Bouden Omar (341381)
 */



public final class AircraftController {
    private static final AircraftTypeDesignator UNKNOWN_DESIGNATOR =  new AircraftTypeDesignator("");
    private static final AircraftDescription UNKNOWN_DESCRIPTION =  new AircraftDescription("");
    private static final int ALTITUDE_NORMALISATION_FACTOR = 12_000, DEFAULT_OVERSIZE = 4;
    private static final double ALTITUDE_EXPONENT_NORMALISATION_FACTOR = 1./3;
    private final MapParameters mapParameters;
    private final ObservableSet<ObservableAircraftState> states;
    private final Pane pane;
    private final ObjectProperty<ObservableAircraftState> aircraftWithVisibleProperties;
    private static final int LINE_START = 0;
    private static final int GRADIENT_START_X = 0, GRADIENT_START_Y = 0,
            GRADIENT_END_X = 1, GRADIENT_END_Y = 0;
    private static final int STOP_OFFSET_FIRST = 0, STOP_OFFSET_SECOND = 1;
    private static final int MINIMAL_TRAJECTORY_SIZE = 2;
    private static final int DEFAULT_LINE_WIDTH = 3;

    /**
     * AircraftController's public constructor
     *
     * @param mapParameters (MapParameters) map variant parameters
     * @param states (ObservableSet<ObservableAircraftState>) set of aircraft states to display
     * @param aircraftWithVisibleProperties (ObjectProperty<ObservableAircraftState>) property storing the state of the active plane,
     *                                       i.e., the plane whose trajectory is drawn on every change
     */
    public AircraftController(MapParameters mapParameters, ObservableSet<ObservableAircraftState> states,
                              ObjectProperty<ObservableAircraftState> aircraftWithVisibleProperties) {
        this.mapParameters = mapParameters;
        this.states = states;
        this.aircraftWithVisibleProperties = aircraftWithVisibleProperties;
        pane = new Pane();

        setUpPane();
        setUpListeners();
    }

    /**
     * @return pane (Pane) object that represents the map pane.
     */
    public Pane pane(){
        return pane;
    }
    private void setUpPane(){
        pane.getStylesheets().add("aircraft.css");
        pane.setPickOnBounds(false);
    }
    private Shape icon(ObservableAircraftState state){
        AircraftData aircraftData = state.getAircraftData();
        SVGPath svgPath = new SVGPath();
        AircraftIcon icon;
        if(aircraftData!=null)
            icon = AircraftIcon.iconFor(aircraftData.typeDesignator(), aircraftData.description(),
                    state.getCategory(), aircraftData.wakeTurbulenceCategory());
        else icon = AircraftIcon.iconFor(UNKNOWN_DESIGNATOR, UNKNOWN_DESCRIPTION, state.getCategory(), WakeTurbulenceCategory.UNKNOWN );
        svgPath.setContent(icon.svgPath());
        svgPath.getStyleClass().add("aircraft");
        if(icon.canRotate())
            svgPath.rotateProperty().bind(Bindings.createObjectBinding(
                    () -> Units.convertTo(state.getTrackOrHeading(), Units.Angle.DEGREE), state.trackOrHeadingProperty()
            ));
        svgPath.fillProperty().bind(state.altitudeProperty().map(b -> ColorFromAltitude((Double) b)));

        svgPath.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> aircraftWithVisibleProperties.set(state));
        return svgPath;
    }
    private Group label(ObservableAircraftState state){
        Rectangle rectangle = new Rectangle();
        Text text = new Text();
        text.textProperty().bind(
                Bindings.createStringBinding(() ->
                        String.format("%s\n%s km/h\u2002%s m", firstLineInRectangle(state)
                                , Double.isNaN(state.getVelocity()) ? "?" : String.valueOf((int) Math.round(Units.convertTo(state.getVelocity(), Units.Speed.KILOMETER_PER_HOUR)))
                                , (int)Math.round(state.getAltitude())) ,
                        state.altitudeProperty(), state.velocityProperty(), state.callSignProperty()));
        rectangle.widthProperty().bind( text.layoutBoundsProperty().map( b -> b.getWidth()+DEFAULT_OVERSIZE ) );
        rectangle.heightProperty().bind( text.layoutBoundsProperty().map( b -> b.getHeight()+DEFAULT_OVERSIZE ) );
        Group group = new Group(rectangle, text);
        group.getStyleClass().add("label");
        group.visibleProperty().bind(aircraftWithVisibleProperties.isEqualTo(state).or(mapParameters.getZoomProperty().greaterThan(11)));
        return group;
    }
    private Group trajectory(ObservableAircraftState state){
        Group trajectoryGroup = new Group();
        trajectoryGroup.visibleProperty().bind(aircraftWithVisibleProperties.isEqualTo(state));


        trajectoryGroup.visibleProperty().addListener(e -> {
            if(trajectoryGroup.isVisible()) constructAllTrajectoryLines(trajectoryGroup, state.getTrajectory());
        });
        state.getTrajectory().addListener((ListChangeListener<AirbornePos>) change -> {
            if(trajectoryGroup.isVisible()) {
                int size = state.getTrajectory().size();
                if(size>=MINIMAL_TRAJECTORY_SIZE)
                    trajectoryGroup.getChildren().add(lineFromTwoAirbornePos(state.getTrajectory().get(size-MINIMAL_TRAJECTORY_SIZE), state.getTrajectory().get(size-MINIMAL_TRAJECTORY_SIZE+1)));
            }
        });
        mapParameters.getZoomProperty().addListener(e -> {
            if(trajectoryGroup.isVisible()) constructAllTrajectoryLines(trajectoryGroup, state.getTrajectory());
        });
        trajectoryGroup.layoutXProperty().bind(mapParameters.getMinXProperty().negate());
        trajectoryGroup.layoutYProperty().bind(mapParameters.getMinYProperty().negate());
        return trajectoryGroup;
    }
    private Group group(ObservableAircraftState state){
        Group iconLabelGroup = new Group(icon(state), label(state));
        iconLabelGroup.layoutXProperty().bind(Bindings.createDoubleBinding(
                () -> calculateXInPane(state.getPosition())
                , mapParameters.getZoomProperty(), mapParameters.getMinXProperty(), state.positionProperty()
        ));
        iconLabelGroup.layoutYProperty().bind(Bindings.createDoubleBinding(
                () -> calculateYInPane(state.getPosition())
                ,mapParameters.getZoomProperty() , mapParameters.getMinYProperty(), state.positionProperty()
        ));
        Group aircraftGroup = new Group(trajectory(state), iconLabelGroup);
        aircraftGroup.setId(state.address().string());
        aircraftGroup.viewOrderProperty().bind(state.altitudeProperty().negate());
        return aircraftGroup;
    }
    private Color ColorFromAltitude(double alt){
        double value = Math.pow(alt/ALTITUDE_NORMALISATION_FACTOR, ALTITUDE_EXPONENT_NORMALISATION_FACTOR);
        return ColorRamp.PLASMA.at(value);
    }


    private String firstLineInRectangle(ObservableAircraftState state){
        AircraftData aircraftData = state.getAircraftData();
        if(aircraftData!=null){
            if(!aircraftData.registration().string().isEmpty()) return aircraftData.registration().string();
            if(!state.getCallSign().string().isEmpty()) return state.getCallSign().string();
        }
        return state.address().string();
    }
    private void constructAllTrajectoryLines(Group trajectoryGroup, ObservableList<AirbornePos> trajectory){
        List<Node> groupChildren = trajectoryGroup.getChildren();
        groupChildren.clear();
        AirbornePos prev = null;
        for(AirbornePos pos : trajectory){
            if(prev!=null) {
                Line line = lineFromTwoAirbornePos(prev, pos);
                groupChildren.add(line);
            }
            prev = pos;
        }
    }
    private Line lineFromTwoAirbornePos(AirbornePos first, AirbornePos second){
        double xFirst = calculateXInPane(first.position()),
                xSecond = calculateXInPane(second.position()),
                yFirst = calculateYInPane(first.position()),
                ySeconde = calculateYInPane(second.position());
        Line line = new Line(LINE_START, LINE_START, xSecond-xFirst, ySeconde-yFirst);
        if(first.altitude()== second.altitude()) line.setStroke(ColorFromAltitude(first.altitude()));
        else {
            Stop s1 = new Stop(STOP_OFFSET_FIRST, ColorFromAltitude(first.altitude()));
            Stop s2 = new Stop(STOP_OFFSET_SECOND, ColorFromAltitude(second.altitude()));
            line.setStroke(new LinearGradient(GRADIENT_START_X, GRADIENT_START_Y, GRADIENT_END_X, GRADIENT_END_Y, true, NO_CYCLE, s1, s2));
        }
        line.setStrokeWidth(DEFAULT_LINE_WIDTH);
        int zoom = mapParameters.getZoom();
        line.setStartX( WebMercator.x(zoom, first.position().longitude()) );
        line.setStartY( WebMercator.y(zoom, first.position().latitude()) );
        line.setEndX( WebMercator.x(zoom, second.position().longitude()) );
        line.setEndY( WebMercator.y(zoom, second.position().latitude()) );
        return line;
    }
    private void setUpListeners(){
        states.addListener( (SetChangeListener<ObservableAircraftState>) change -> {
            if(change.wasAdded())
                pane.getChildren().add(group(change.getElementAdded()));
            else if(change.wasRemoved()) removeGroupFromIcaoAddress(change.getElementRemoved().address());
        });
    }
    private void removeGroupFromIcaoAddress(IcaoAddress icaoAddress){
        Node groupToRemove = pane.lookup("#"+icaoAddress.string());
        pane.getChildren().remove(groupToRemove);
    }


    private double calculateXInPane(GeoPos geoPos){
        double x = WebMercator.x(mapParameters.getZoom(), geoPos.longitude());
        return x-mapParameters.getMinX();
    }
    private double calculateYInPane(GeoPos geoPos){
        double y = WebMercator.y(mapParameters.getZoom(), geoPos.latitude());
        return y-mapParameters.getMinY();
    }


}
