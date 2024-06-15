package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.WebMercator;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;


/**
 * The AntennaReceiver class represents an antenna receiver on a map.
 * <p>
 * It displays the antenna position and circles representing distances from the antenna.
 * @author: Tlili Ahmed (344939)
 * @author: Bouden Omar (341381)
 */
public class AntennaReceiver {
    private static final GeoPos POINT_1_100KM = new GeoPos(80323379, 550195825);
    private static final GeoPos POINT_2_100KM = new GeoPos(80323379, 539466500);
    private static final String svgPath =
        """
        M12 5c-3.87 0-7 3.13-7 7h2c0-2.76 2.24-5 5-5s5 2.24 5 5h2c0-3.87-3.13-7-7-7zm1 9.29c.88-.39 1.5-1.26
        1.5-2.29 0-1.38-1.12-2.5-2.5-2.5S9.5 10.62 9.5 12c0 1.02.62 1.9 1.5 2.29v3.3L7.59 21 9 22.41l3-3 3
        3L16.41 21 13 17.59v-3.3zM12 1C5.93 1 1 5.93 1 12h2c0-4.97 4.03-9 9-9s9 4.03 9 9h2c0-6.07-4.93-11-11-11z
        """;
    private final Pane pane;
    private final MapParameters mapParameters;
    private ObjectProperty<GeoPos> antennaPos;
    private DoubleProperty hundredKilometersRadius;
    private final SVGPath svgPath1;

    /**
     * Constructs an AntennaReceiver object with the specified map parameters.
     * @param mapParameters The map parameters.
     */
    public AntennaReceiver(MapParameters mapParameters){
        svgPath1 = new SVGPath();
        svgPath1.setContent(svgPath);
        antennaPos = new SimpleObjectProperty<>(null);
        hundredKilometersRadius = new SimpleDoubleProperty();
        pane = new Pane();
        pane.setPickOnBounds(false);
        this.mapParameters = mapParameters;
        antennaPos.addListener((p,o,n) -> {
            if(n!=null) {
                pane.visibleProperty().set(true);
                svgPath1.layoutXProperty().bind(Bindings.createDoubleBinding(
                        () -> calculateXInPane(n) - svgPath1.getBoundsInLocal().getWidth()/2, mapParameters.getMinXProperty(), mapParameters.getZoomProperty()
                ));
                svgPath1.layoutYProperty().bind(Bindings.createDoubleBinding(
                        () -> calculateYInPane(n) - svgPath1.getBoundsInLocal().getHeight()/2, mapParameters.getMinYProperty(), mapParameters.getZoomProperty()
                ));

            hundredKilometersRadius.bind(Bindings.createDoubleBinding(
                    () -> Math.abs(calculateYInPane(POINT_1_100KM) - calculateYInPane(POINT_2_100KM))
            , mapParameters.getZoomProperty()
            ));
            } else pane.visibleProperty().set(false);
        });
        pane.getChildren().addAll(svgPath1, createCircleWithDistanceFromAntenna(100)
                , createCircleWithDistanceFromAntenna(150), createCircleWithDistanceFromAntenna(200), createCircleWithDistanceFromAntenna(250));
        pane.visibleProperty().set(false);



    }

    /**
     * Returns the Pane containing the antenna receiver.
     * @return The Pane.
     */
    public Pane pane(){
        return pane;
    }

    /**
     * Returns the ObjectProperty for the antenna position.
     * @return The ObjectProperty for the antenna position.
     */
    public ObjectProperty<GeoPos> antennaPosProperty(){
        return antennaPos;
    }


    private Circle createCircleWithDistanceFromAntenna(double distanceInKilometers){
        Circle circle = new Circle();
        circle.radiusProperty().bind(hundredKilometersRadius.multiply(distanceInKilometers/100));
        circle.setFill(Color.TRANSPARENT);
        circle.setMouseTransparent(true);
        circle.setStroke(Color.RED);
        circle.setStrokeWidth(3);
        circle.setPickOnBounds(false);
        circle.layoutXProperty().bind(Bindings.createDoubleBinding(
                () -> svgPath1.layoutXProperty().get() + svgPath1.getBoundsInLocal().getWidth()/2 , svgPath1.layoutXProperty()
        ));
        circle.layoutYProperty().bind(Bindings.createDoubleBinding(
                () -> svgPath1.layoutYProperty().get() + svgPath1.getBoundsInLocal().getHeight()/2, svgPath1.layoutYProperty()
        ));
        return circle;
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
