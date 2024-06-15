package ch.epfl.javions.gui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;



/**
 * Controller class for map buttons.
 * Buttons include zoom in/out and aircraft lock in/out.
 * @author: Tlili Ahmed (344939)
 * @author: Bouden Omar (341381)
 */
public class MapButtonsController {
    // after compiling the rapport.pdf from latex, i lost the original source file
    // so i forgot to mention that i added new listeners in the MapParameters class
    // - added listener to zoom on trackpadZoom
    // - when moving throughout the map with mouse the mouse icon changes
    private static final int BUTTON_OFFSET_FROM_CORNERS = 4, BUTTON_SIZE = 30;
    private final MapParameters mapParameters;
    private final BaseMapController bmc;
    private final ObjectProperty<ObservableAircraftState> sap;
    private final Pane pane;


    /**
     * Constructs a new MapButtonsController with the specified map parameters, base map controller,
     * and object property of observable aircraft state.
     *
     * @param mapParameters the map parameters
     * @param bmc           the base map controller
     * @param sap           the object property of observable aircraft state
     */

    public MapButtonsController(MapParameters mapParameters, BaseMapController bmc, ObjectProperty<ObservableAircraftState> sap){
        this.mapParameters = mapParameters;
        pane = new Pane();
        pane.setPickOnBounds(false);

        try {
            setUpZoomButtons();
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
        this.bmc = bmc;
        this.sap = sap;
    }


    /**
     * Gets the pane associated with this MapButtonsController.
     *
     * @return pane (Pane)
     */
    public Pane pane(){
        return pane;
    }



    private void setUpZoomButtons() throws FileNotFoundException {
        Button zoomInButton = new Button();
        zoomInButton.setGraphic(new Text("+"));
        Button zoomOutButton = new Button();
        zoomOutButton.setGraphic(new Text("-"));
        var image = new ImageView(new Image(new FileInputStream("resources/lock-icon.png")));
        image.setFitWidth(10);
        image.setFitHeight(10);
        ToggleButton lockOnPlane = new ToggleButton("", image);


        zoomInButton.layoutXProperty().bind(pane.widthProperty().map(value ->  value.doubleValue() - BUTTON_OFFSET_FROM_CORNERS - zoomInButton.getWidth()));
        zoomInButton.layoutYProperty().set(BUTTON_OFFSET_FROM_CORNERS);
        zoomInButton.setPrefSize(BUTTON_SIZE,BUTTON_SIZE);

        zoomOutButton.layoutXProperty().bind(zoomInButton.layoutXProperty());
        zoomOutButton.layoutYProperty().bind(zoomInButton.layoutYProperty().add(zoomInButton.heightProperty().add(BUTTON_OFFSET_FROM_CORNERS)));
        zoomOutButton.setPrefSize(BUTTON_SIZE, BUTTON_SIZE);

        lockOnPlane.layoutXProperty().bind(zoomInButton.layoutXProperty());
        lockOnPlane.layoutYProperty().bind(zoomOutButton.layoutYProperty().add(zoomOutButton.heightProperty().add(BUTTON_OFFSET_FROM_CORNERS)));
        lockOnPlane.setPrefSize(BUTTON_SIZE, BUTTON_SIZE);

        zoomInButton.setOnAction(event -> {
            double x = pane.getWidth()/2;
            double y = pane.getHeight()/2;
            mapParameters.scroll(x, y);
            mapParameters.changeZoomLevel(1);
            mapParameters.scroll(-x,-y);
        });

        zoomOutButton.setOnAction(event -> {
            double x = pane.getWidth()/2;
            double y = pane.getHeight()/2;
            mapParameters.scroll(x, y);
            mapParameters.changeZoomLevel(-1);
            mapParameters.scroll(-x, -y);
        });




        Map<ObservableAircraftState, ListChangeListener<ObservableAircraftState.AirbornePos>> listenerMap = new HashMap<>();
        ChangeListener<ObservableAircraftState> listener = (p, o, n) -> {
            if (o != null && listenerMap.containsKey(o)) {
                ListChangeListener<ObservableAircraftState.AirbornePos> oldListener = listenerMap.get(o);
                o.getTrajectory().removeListener(oldListener);
                listenerMap.remove(o);
            }

            if (n != null) {
                bmc.centerOn(n.getTrajectory().get(n.getTrajectory().size() - 1).position());
                ListChangeListener<ObservableAircraftState.AirbornePos> newListener = change ->
                        bmc.centerOn(n.getTrajectory().get(n.getTrajectory().size() - 1).position());
                n.getTrajectory().addListener(newListener);
                listenerMap.put(n, newListener);
            }
        };
        lockOnPlane.setOnAction(event -> {
            if(lockOnPlane.isSelected()){
                sap.addListener(listener);
                if(sap.get()!=null){
                    bmc.centerOn(sap.get().getTrajectory().get(sap.get().getTrajectory().size() - 1).position());
                    ListChangeListener<ObservableAircraftState.AirbornePos> newListener = change ->
                            bmc.centerOn(sap.get().getTrajectory().get(sap.get().getTrajectory().size() - 1).position());
                    sap.get().getTrajectory().addListener(newListener);
                    listenerMap.put(sap.get(), newListener);
                }
            }else {
                sap.get().getTrajectory().removeListener(listenerMap.get(sap.get()));
                sap.removeListener(listener);
            }
        });




        pane.getChildren().addAll(zoomInButton, zoomOutButton, lockOnPlane);

    }

}
