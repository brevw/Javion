package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;


/**
 * This class represents the aircraft description and additional informationss
 *
 * @author: Bouden Omar (341381)
 * @author: Tlili Ahmed (344939)
 */
public record AircraftDescription(String string) {
    /**
     * pattern of all valid aircraft description
     */
    private static final Pattern pattern = Pattern.compile("[ABDGHLPRSTV-][0123468][EJPT-]");

    /**
     * public AircraftDescription compact constructor *
     *
     * @throws IllegalArgumentException when the given string is not a valid aircraft description. Can be empty * @param string (String) given aircraft description. Can be an empty string
     */
    public AircraftDescription {
        Preconditions.checkArgument((string.isEmpty()|| pattern.matcher(string).matches()));
    }
}
