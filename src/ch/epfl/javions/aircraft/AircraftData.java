/**
 * This record represents the aircraft's data
 *
 * @author: Bouden Omar (341381)
 * @author: Tlili Ahmed (344939)
 */
package ch.epfl.javions.aircraft;

import java.util.Objects;

public record AircraftData(AircraftRegistration registration,
                           AircraftTypeDesignator typeDesignator,
                           String model,
                           AircraftDescription description,
                           WakeTurbulenceCategory wakeTurbulenceCategory) {
    /**
     * public AircraftData compact constructor
     *
     * @param typeDesignator (AircraftTypeDesignator), can be empty * @param model (String)
     * @param description    (AircraftDescription), can be empty * @param wakeTurbulenceCategory (WakeTurbulenceCategory)
     * @throws NullPointerException if the given file name corresponds to a null file * @param registration (AircraftRegistration)
     */
    public AircraftData {
        Objects.requireNonNull(registration);
        Objects.requireNonNull(typeDesignator);
        Objects.requireNonNull(description);
        Objects.requireNonNull(model);
        Objects.requireNonNull(wakeTurbulenceCategory);
    }

}
