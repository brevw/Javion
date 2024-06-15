package ch.epfl.javions;

/**
 * This class contains a static method for checking if the preconditions are met
 * else we will have an IllegalArgumentException
 *
 * @author: Bouden Omar (341381)
 * @author: Tlili Ahmed (344939)
 */

public final class Preconditions {
    private Preconditions() {
    }

    /**
     * Checks if the given boolean argument is true
     *
     * @param shouldBeTrue (boolean): given condition which validity we want to check
     * @throws IllegalArgumentException if the precondition is not
     *                                  satisfied (thus if the boolean argument is false)
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) throw new IllegalArgumentException();
    }
}
