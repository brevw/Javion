package ch.epfl.javions;


/**
 * This record represents geographic coordinates stored as 32-bit integers
 *
 * @author: Bouden Omar (341381)
 * @author: Tlili Ahmed (344939)
 */
public record GeoPos(int longitudeT32, int latitudeT32) {
    private static final int UPPER_BOUND_FOR_LATITUDE_T32_IN_ABS = 1 << 30;

    /**
     * public GeoPos compact constructor
     *
     * @param longitudeT32 (int) longitude expressed in T32
     * @param latitudeT32  (int) latitude expressed in T32
     * @throws IllegalArgumentException if latitude in T32 is invalid
     */
    public GeoPos {
        Preconditions.checkArgument(isValidLatitudeT32(latitudeT32));
    }

    /**
     * @param latitudeT32 (int) latitude expressed in T32
     * @return (boolean) true if latitudeT32 is a valid latitude expressed in T32 and false otherwise
     */
    public static boolean isValidLatitudeT32(int latitudeT32) {
        return Math.abs(latitudeT32) <= UPPER_BOUND_FOR_LATITUDE_T32_IN_ABS;
    }

    /**
     * @return longitude in radian
     */
    public double longitude() {
        return Units.convertFrom(longitudeT32, Units.Angle.T32);
    }

    /**
     * @return latitude in radian
     */
    public double latitude() {
        return Units.convertFrom(latitudeT32, Units.Angle.T32);
    }

    @Override
    public String toString() {
        return "(" + Units.convert(longitudeT32, Units.Angle.T32, Units.Angle.DEGREE)
                + "°, " + Units.convert(latitudeT32, Units.Angle.T32, Units.Angle.DEGREE) + "°)";
    }

}
