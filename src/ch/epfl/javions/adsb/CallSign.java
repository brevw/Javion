/**
 * This record represents a call sign
 *
 * @author: Bouden Omar (341381)
 * @author: Tlili Ahmed (344939)
 */
package ch.epfl.javions.adsb;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

public record CallSign(String string) {
    private static final Pattern pattern = Pattern.compile("[A-Z0-9 ]{0,8}");

    /**
     * public CallSign compact constructor
     *
     * @param string (String) given call sign. Can be empty
     * @throws IllegalArgumentException when the given string is not a valid Call sign
     */
    public CallSign {
        Preconditions.checkArgument(pattern.matcher(string).matches());
    }
}
