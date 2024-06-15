/**
 * This record represents an aircraft type designator
 *
 * @author: Bouden Omar (341381)
 * @author: Tlili Ahmed (344939)
 */
package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

public record AircraftTypeDesignator(String string) {
    /**
     * Pattern of all valid aircraft type designator addresses. Can be empty.
     */
    private static final Pattern pattern = Pattern.compile("[A-Z0-9]{2,4}");

    /**
     * AircraftTypeDesignator compact constructor
     *
     * @param string (String) given aircraft type designator given to check.Can be an empty string.
     */
    public AircraftTypeDesignator {
        Preconditions.checkArgument(pattern.matcher(string).matches() || string.isEmpty());
    }
}
