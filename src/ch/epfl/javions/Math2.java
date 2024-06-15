package ch.epfl.javions;
/**
 * This class contains static methods useful for performing mathematical operations
 *
 * @author: Bouden Omar (341381)
 * @author: Tlili Ahmed (344939)
 */


public final class Math2 {
    private Math2() {
    }

    /**
     * @param min (int)
     * @param v   (int)
     * @param max (int)
     * @return v if it is contained in the range. Otherwise, it returns
     * the closest bound of the range to v.
     * @throws IllegalArgumentException if the given range in invalid
     */
    public static int clamp(int min, int v, int max) {
        Preconditions.checkArgument(min <= max);
        return Math.max(min, Math.min(v, max));
    }

    /**
     * Calculates the inverse hyperbolic sine of x
     *
     * @param x (double) value
     * @return the inverse hyperbolic sine of x
     */
    public static double asinh(double x) {
        return Math.log(x + Math.hypot(1, x));
    }
}
