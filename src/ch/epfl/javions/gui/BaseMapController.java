package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.WebMercator;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import java.io.IOException;


/**
 * This class controls the map behavior in every way. It is responsible for showing all graphics related to the map (stored on a pane).
 * <p>
 * It contains methods to center the map on a given position and a getter method for the map pane.
 * <p>
 * The class also has listeners and handlers to update the map when any changes occur, such as zooming, scrolling, or
 * dragging.
 *
 * @author: Tlili Ahmed (344939)
 * @author: Bouden Omar (341381)
 */
public final class BaseMapController {
    private static final int ZOOM_WAITING_TIME = 200;
    private static final int IMAGE_WIDTH_HEIGHT = 256;
    private final TileManager tileManager;
    private final MapParameters mapParameters;
    private final Pane pane;
    private final Canvas canvas;
    private final GraphicsContext graphicsContext;
    private boolean redrawNeeded;
    private final LongProperty minScrollTime;
    private final ObjectProperty<Point2D> lastMousePos;

    /**
     * BaseMapController's public constructor
     *
     * @param tileManager A TileManager instance that provides the images to be displayed on the map.
     * @param mapParameters A MapParameters instance that contains the parameters defining the current state of the map.
     * */

    public BaseMapController(TileManager tileManager, MapParameters mapParameters) {
        this.tileManager = tileManager;
        this.mapParameters = mapParameters;
        minScrollTime = new SimpleLongProperty();
        lastMousePos = new SimpleObjectProperty<>();

        canvas = new Canvas();
        graphicsContext = canvas.getGraphicsContext2D();
        pane = new Pane(canvas);
        redrawNeeded = true;

        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());



        setUpListeners();
        setUpEventHandler();
    }

    /**
     * pane getter
     * @return pane (Pane) containing all graphical elements of the class
     */
    public Pane pane(){
        return pane;
    }

    /**
     * given a position, center it in the middle of our canvas
     * @param geoPos (GeoPos)
     */
    public void centerOn(GeoPos geoPos){
        double x = WebMercator.x(mapParameters.getZoom(), geoPos.longitude());
        double y = WebMercator.y(mapParameters.getZoom(), geoPos.latitude());
        double xCenterOfScreen = mapParameters.getMinX() + canvas.getWidth()/2;
        double yCenterOfScreen = mapParameters.getMinY() + canvas.getHeight()/2;
        mapParameters.scroll(x-xCenterOfScreen, y-yCenterOfScreen);
    }
    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;
        int initialTileXComponent = (int) Math.floor(mapParameters.getMinX()/IMAGE_WIDTH_HEIGHT);
        int initialTileYComponent = (int) Math.floor(mapParameters.getMinY()/IMAGE_WIDTH_HEIGHT);
        double xOffset = initialTileXComponent*IMAGE_WIDTH_HEIGHT - mapParameters.getMinX();
        double yOffset = initialTileYComponent*IMAGE_WIDTH_HEIGHT - mapParameters.getMinY();
        int imagesNumberOnX = (int)Math.ceil(canvas.getWidth()/IMAGE_WIDTH_HEIGHT);
        int imagesNumberOnY = (int)Math.ceil(canvas.getHeight()/IMAGE_WIDTH_HEIGHT);

        for(int i=0;i<=imagesNumberOnX;++i){
            for(int j=0;j<=imagesNumberOnY;++j){
                int x = initialTileXComponent+i;
                int y = initialTileYComponent+j;
                if (TileManager.TileId.isValid(mapParameters.getZoom(), x, y))
                    try {
                        Image image = tileManager.imageForTileAt(
                                new TileManager.TileId(mapParameters.getZoom(), x, y));
                        graphicsContext.drawImage(image, xOffset+IMAGE_WIDTH_HEIGHT*i,yOffset+IMAGE_WIDTH_HEIGHT*j);
                    } catch (IOException ignored) {}
            }
        }
    }
    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }
    private void setUpListeners(){
        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });
        mapParameters.getZoomProperty().addListener((p,o,n) -> redrawOnNextPulse());
        mapParameters.getMinXProperty().addListener((p,o,n) -> redrawOnNextPulse());
        mapParameters.getMinYProperty().addListener((p,o,n) -> redrawOnNextPulse());
        canvas.widthProperty().addListener((p,o,n) -> redrawOnNextPulse());
        canvas.heightProperty().addListener((p,o,n) -> redrawOnNextPulse());
    }
    private void setUpEventHandler(){


        pane.setOnScroll(e -> {
            int zoomDelta = (int) Math.signum(e.getDeltaY());
            if (zoomDelta == 0) return;
            long currentTime = System.currentTimeMillis();
            if(!(currentTime >= minScrollTime.get())) return;
            minScrollTime.set(currentTime + ZOOM_WAITING_TIME);
            changeZoomWithMouseAsFixedPoint(e.getX(), e.getY(), zoomDelta);
        });
        pane.setOnMousePressed(e -> {
            updateMouseCoordsWithoutRedrawNeeded(e.getX(), e.getY());
            if(canvas.getCursor()!=Cursor.CLOSED_HAND)  canvas.setCursor(Cursor.CLOSED_HAND);
        });
        pane.setOnMouseDragged(e -> {
            mapParameters.scroll(lastMousePos.get().getX()-e.getX(), lastMousePos.get().getY()-e.getY());
            updateMouseCoordsWithoutRedrawNeeded(e.getX(), e.getY());
            redrawOnNextPulse();
        });
        pane.setOnMouseReleased(e -> {
            if(canvas.getCursor()!=Cursor.DEFAULT) canvas.setCursor(Cursor.DEFAULT);
            lastMousePos.set(null);
        });
        pane.setOnZoom(e -> {
            long currentTime = System.currentTimeMillis();
            if(!(currentTime >= minScrollTime.get())) return;
            minScrollTime.set(currentTime + ZOOM_WAITING_TIME);
            changeZoomWithMouseAsFixedPoint(e.getX(), e.getY(), e.getZoomFactor()>1 ? 1 : -1);
        });
    }
    private void changeZoomWithMouseAsFixedPoint(double xMouse, double yMouse, int zoomDiff){
        mapParameters.scroll(xMouse, yMouse);
        mapParameters.changeZoomLevel(zoomDiff);
        mapParameters.scroll(-xMouse,-yMouse);
    }
    private void updateMouseCoordsWithoutRedrawNeeded(double xMouse, double yMouse){
        lastMousePos.set(new Point2D(xMouse, yMouse));
    }
}


