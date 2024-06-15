package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

/**
 * This record represents an ICAO address
 *
 * @author: Bouden Omar (341381)
 * @author: Tlili Ahmed (344939)
 */
public record IcaoAddress(String string) {
    public static final int ICAO_ADDRESS_LENGTH = 6;
    /**
     * pattern of all valid ICAO addresses
     */
    private static final Pattern pattern = Pattern.compile("[0-9A-F]{6}");

    /**
     * public IcaoAddress compact constructor
     *
     * @throws IllegalArgumentException when the given string is not a valid ICAO address * @param string (String) given ICAO address
     */
    public IcaoAddress {
        Preconditions.checkArgument(pattern.matcher(string).matches());
    }
}
