package ch.epfl.javions.gui;

import ch.epfl.javions.Math2;
import ch.epfl.javions.Preconditions;
import javafx.beans.property.*;



/**
 * This class represents the parameters of a map, including the current zoom level and the position of the top-left
 * corner of the map on the x-y plane.
 * @author: Tlili Ahmed (344939)
 * @author: Bouden Omar (341381)
 */
public final class MapParameters {
    private static final int ZOOM_LOWER_BOUND = 6, ZOOM_UPPER_BOUND = 19;
    private final IntegerProperty zoom;
    private final DoubleProperty minX, minY;


    /**
     * Constructs a new `MapParameters` object with the specified zoom level and position.
     *
     * @param zoom The initial zoom level of the map.
     * @param minX The initial x-coordinate of the top-left corner of the map.
     * @param minY The initial y-coordinate of the top-left corner of the map.
     * @throws IllegalArgumentException If the specified zoom level is not within the range of
     *                                  {@code ZOOM_LOWER_BOUND} and {@code ZOOM_UPPER_BOUND}.
     */
    public MapParameters(int zoom, double minX, double minY){
        Preconditions.checkArgument(ZOOM_LOWER_BOUND<=zoom && zoom<=ZOOM_UPPER_BOUND);
        this.zoom = new SimpleIntegerProperty(zoom);
        this.minX = new SimpleDoubleProperty(minX);
        this.minY = new SimpleDoubleProperty(minY);
    }

    /**
     * Gets the current zoom level of the map.
     *
     * @return The current zoom level.
     */
    public int getZoom(){
        return zoom.get();
    }


    /**
     * Gets the property representing the current zoom level of the map.
     *
     * @return The property representing the current zoom level.
     */
    public ReadOnlyIntegerProperty getZoomProperty(){
        return zoom;
    }


    /**
     * Gets the x-coordinate of the top-left corner of the map.
     *
     * @return The x-coordinate of the top-left corner of the map.
     */
    public double getMinX(){
        return minX.get();
    }


    /**
     * Gets the property representing the x-coordinate of the top-left corner of the map.
     *
     * @return The property representing the x-coordinate of the top-left corner of the map.
     */
    public ReadOnlyDoubleProperty getMinXProperty(){
        return minX;
    }


    /**
     * Gets the y-coordinate of the top-left corner of the map.
     *
     * @return The y-coordinate of the top-left corner of the map.
     */
    public double getMinY(){
        return minY.get();
    }


    /**
     * Gets the property representing the y-coordinate of the top-left corner of the map.
     *
     * @return The property representing the y-coordinate of the top-left corner of the map.
     */
    public ReadOnlyDoubleProperty getMinYProperty(){
        return minY;
    }

    /**
     * Scrolls the map by the specified amount in the x and y directions.
     *
     * @param x The amount to scroll in the x direction.
     * @param y The amount to scroll in the y direction.
     */
    public void scroll(double x, double y){
        if(!(x==0 && y==0)){
            minX.set(getMinX() + x);
            minY.set(getMinY() + y);
        }
    }

    /**
     * Changes the zoom level of the map by the specified amount.
     *
     * @param zoomDiff The amount to change the zoom level by.
     */
    public void changeZoomLevel(int zoomDiff){
        int newZoom = Math2.clamp(ZOOM_LOWER_BOUND, getZoom() + zoomDiff, ZOOM_UPPER_BOUND);
        double minVariation = Math.scalb(1, newZoom - getZoom());
        if(newZoom != getZoom()){
            minX.set(getMinX() * minVariation);
            minY.set(getMinY() * minVariation);
            zoom.set(newZoom);
        }
    }


}
