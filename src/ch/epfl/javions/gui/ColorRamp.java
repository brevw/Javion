package ch.epfl.javions.gui;

import ch.epfl.javions.Preconditions;
import javafx.scene.paint.Color;


/**
 * A utility class for creating color ramps for visualizing data.
 * A color ramp is a sequence of colors that are used to represent data
 * values in a continuous gradient. The colors in the ramp are specified
 * by a set of control points, and the ramp can be interpolated between
 * these points to generate colors for intermediate values.
 * <p>
 * The ColorRamp class provides methods for creating and interpolating
 * color ramps. It includes pre-defined ramp for common color maps called "plasma".
 *
 * @author: Tlili Ahmed (344939)
 * @author: Bouden Omar (341381)
 */

public final class ColorRamp {

    /**
     * A pre-defined color ramp that represents the "plasma" color map.
     * This color ramp consists of 30 different colors ranging from dark blue
     * to bright yellow.
     * <p>
     * The color ramp is defined by a sequence of Color objects, each of which
     * represents a point along the ramp. The first color corresponds to the
     * lowest value in the data range, while the last color corresponds to the
     * highest value in the data range.
     */
    public static final ColorRamp PLASMA = new ColorRamp(
            Color.valueOf("0x0d0887ff"), Color.valueOf("0x220690ff"),
            Color.valueOf("0x320597ff"), Color.valueOf("0x40049dff"),
            Color.valueOf("0x4e02a2ff"), Color.valueOf("0x5b01a5ff"),
            Color.valueOf("0x6800a8ff"), Color.valueOf("0x7501a8ff"),
            Color.valueOf("0x8104a7ff"), Color.valueOf("0x8d0ba5ff"),
            Color.valueOf("0x9814a0ff"), Color.valueOf("0xa31d9aff"),
            Color.valueOf("0xad2693ff"), Color.valueOf("0xb6308bff"),
            Color.valueOf("0xbf3984ff"), Color.valueOf("0xc7427cff"),
            Color.valueOf("0xcf4c74ff"), Color.valueOf("0xd6556dff"),
            Color.valueOf("0xdd5e66ff"), Color.valueOf("0xe3685fff"),
            Color.valueOf("0xe97258ff"), Color.valueOf("0xee7c51ff"),
            Color.valueOf("0xf3874aff"), Color.valueOf("0xf79243ff"),
            Color.valueOf("0xfa9d3bff"), Color.valueOf("0xfca935ff"),
            Color.valueOf("0xfdb52eff"), Color.valueOf("0xfdc229ff"),
            Color.valueOf("0xfccf25ff"), Color.valueOf("0xf9dd24ff"),
            Color.valueOf("0xf5eb27ff"), Color.valueOf("0xf0f921ff"));

    private static final int MIN_NUMBER_OF_COLORS = 2;
    private static final int COLOR_VALUE_UPPER_BOUND = 1, COLOR_VALUE_LOWER_BOUND = 0;
    private final Color[] colors;
    private final int nbrSubDiv;


    /**
     * Creates a new color ramp with the specified control points.
     *
     * @param colors the control points of the color ramp
     * @throws IllegalArgumentException if the number of colors is less than 2
     */
    public ColorRamp(Color... colors) {
        Preconditions.checkArgument(colors.length >= MIN_NUMBER_OF_COLORS);
        this.colors = colors.clone();
        nbrSubDiv = colors.length - 1;
    }

    /**
     * Returns the color corresponding to the specified value in the range [0,1].
     * The value is automatically clamped to the range [0,1].
     *
     * @param value (double)
     * @return the interpolated color
     */
    public Color at(double value){
        if(value<=COLOR_VALUE_LOWER_BOUND) return colors[0];
        if(value>=COLOR_VALUE_UPPER_BOUND) return colors[colors.length-1];
        double index = value*nbrSubDiv;
        int LHSIndex = (int)Math.floor(index);
        Color LHSColor = colors[LHSIndex];
        Color RHSColor = colors[LHSIndex+1];
        return LHSColor.interpolate(RHSColor, (index - LHSIndex));

    }
}
