package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

/**
 * This record represents an aircraft registration address
 *
 * @author: Bouden Omar (341381)
 * @author: Tlili Ahmed (344939)
 */
public record AircraftRegistration(String string) {
    private static final Pattern pattern = Pattern.compile("[A-Z0-9 .?/_+-]+");

    /**
     * public AircraftRegistration compact constructor
     *
     * @param string (String) given aircraft registration
     * @throws IllegalArgumentException when the given string is not a valid aircraft registration
     */
    public AircraftRegistration {
        Preconditions.checkArgument(pattern.matcher(string).matches());
    }
}
