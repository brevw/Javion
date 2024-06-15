package ch.epfl.javions;


/**
 * This class provides static methods for calculating the
 * Web Mercator projection coordinates
 *
 * @author: Bouden Omar (341381)
 * @author: Tlili Ahmed (344939)
 */
public final class WebMercator {

    private static final int ZOOM_LEVEL_OFFSET = 8;
    private static final double LONGITUDE_LATITUDE_OFFSET_IN_TURN = 0.5;


    private WebMercator() {
    }

    /**
     * @param zoomLevel (int) given zoomLevel
     * @param longitude (double) given longitude in radians
     * @return x coordinates of the WebMercator projection
     */
    public static double x(int zoomLevel, double longitude) {
        return Math.scalb(Units.convertTo(longitude, Units.Angle.TURN) + LONGITUDE_LATITUDE_OFFSET_IN_TURN,
                ZOOM_LEVEL_OFFSET + zoomLevel);
    }

    /**
     * @param zoomLevel (int) given zoomLevel
     * @param latitude  (double) given latitude in radians
     * @return y coordinate of the WebMercator projection
     */
    public static double y(int zoomLevel, double latitude) {
        return Math.scalb(-Units.convertTo(Math2.asinh(Math.tan(latitude)),
                Units.Angle.TURN) + LONGITUDE_LATITUDE_OFFSET_IN_TURN, ZOOM_LEVEL_OFFSET + zoomLevel);
    }
}
