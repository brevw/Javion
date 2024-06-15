/**
 * Contains the units' constants and methods to convert values from a unit to another
 *
 * @author: Bouden Omar (341381)
 * @author: Tlili Ahmed (344939)
 */
package ch.epfl.javions;

public final class Units {

    private Units(){

    }

    /**
     * SI prefixes
     */
    public static final double CENTI = 1e-2, KILO = 1e3;

    /**
     * Converts value (initially expressed in fromUnit) to toUnit
     *
     * @param value    to be converted
     * @param fromUnit initial unit of value
     * @param toUnit   the unit we want to express value in
     * @return value expressed in toUnit
     */
    public static double convert(double value, double fromUnit, double toUnit) {
        return value * (fromUnit / toUnit);
    }

    /**
     * @param value    value to be converted
     * @param fromUnit initial unit of value
     * @return value expressed in its corresponding basic unit
     */
    public static double convertFrom(double value, double fromUnit) {
        return convert(value, fromUnit, 1);
    }

    /**
     * @param value  value to be converted. Must be expressed in a basic unit
     * @param toUnit the unit we want to express value in
     * @return value expressed in toUnit
     */
    public static double convertTo(double value, double toUnit) {
        return convert(value, 1, toUnit);
    }

    /**
     * This static inner class contains the angle-related units
     */
    public static class Angle {
        /**
         * basic unit for angle
         */
        public static final double RADIAN = 1;

        private Angle() {
        }
        /**
         * Angle units defined according to the base unit
         */
        public static final double TURN = 2 * Math.PI, DEGREE = TURN / 360, T32 = TURN / Math.scalb(1, 32);
    }

    /**
     * This static inner class contains the length-related units
     */
    public static class Length {
        /**
         * basic unit for length (distance)
         */
        public static final double METER = 1;

        private Length() {
        }
        /**
         * Length units defined according to the base unit
         */
        public static final double CENTIMETER = CENTI * METER, KILOMETER = KILO * METER,
                INCH = 2.54 * CENTIMETER, FOOT = 12 * INCH, NAUTICAL_MILE = 1852 * METER;
    }

    /**
     * This static inner class contains the time-related units
     */
    public static class Time {
        /**
         * basic unit for time
         */

        public static final double SECOND = 1;

        private Time() {
        }
        /**
         * Time units defined according to the base unit
         */
        public static final double MINUTE = 60 * SECOND, HOUR = 60 * MINUTE;
    }

    /**
     * This static inner class contains the speed-related units
     */
    public static class Speed {
        /**
         * Speed units defined according to Time and Length previously defined units
         */
        public static final double KNOT = Length.NAUTICAL_MILE / Time.HOUR,
                KILOMETER_PER_HOUR = Length.KILOMETER / Time.HOUR;

        private Speed() {
        }
    }


}
