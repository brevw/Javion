package ch.epfl.javions.aircraft;
/**
 * This enum type represents turbulence categories by aircraft
 * we enumerate WakeTurbulence to 4 categories
 *
 * @author: Bouden Omar (341381)
 * @author: Tlili Ahmed (344939)
 */

public enum WakeTurbulenceCategory {
    LIGHT, MEDIUM, HEAVY, UNKNOWN;

    /**
     * @param s (String) * @return (WakeTurbulenceCategory) the turbulence category
     *          corresponding to the given s string
     */
    public static WakeTurbulenceCategory of(String s) {
        return switch (s) {
            case "L" -> LIGHT;
            case "M" -> MEDIUM;
            case "H" -> HEAVY;
            default -> UNKNOWN;
        };
    }
}
