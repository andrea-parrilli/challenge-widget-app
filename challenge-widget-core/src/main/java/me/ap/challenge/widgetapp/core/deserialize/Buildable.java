package me.ap.challenge.widgetapp.core.deserialize;

/**
 * The trait of a builder (as in the pattern).
 * <p/>
 * Although not strictly necessary, it is required that the instance produced by {@link #build()} is a {@link ToBuilderable}.
 * This makes the two traits complement each other, and simplifies reasoning about modification of such instances,
 * since a class and its builder become logically interchangeable.
 * <br/>
 * The motivation for this type and {@link ToBuilderable} is to streamline the interaction of Lombok and Jackson when
 * using immutable DTOs. By making the DTOs implement {@link ToBuilderable}, and their (generated) builders {@link Buildable},
 * we can obtain a modifiable version of a DTO that is completely equivalent to it, and that can be updated and built
 * back to the original DTO type with very little effort.
 *
 * @param <T> the type produced by the builder, which must itself be a {@link ToBuilderable}.
 */
public interface Buildable<T extends ToBuilderable<? extends Buildable<T>>> {
    /**
     * Call this method to make an instance of {@code T} with the property values set on this builder.
     * <br/>
     * This method is reentrant, i.e. repeated calls to this method must produce instances that are equal (no necessary
     * the same).
     *
     * @return an instance that with properties as set in this builder.
     */
    default T build() {
        return null;
    }
}
