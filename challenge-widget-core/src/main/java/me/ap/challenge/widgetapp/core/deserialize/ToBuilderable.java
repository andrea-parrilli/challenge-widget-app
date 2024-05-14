package me.ap.challenge.widgetapp.core.deserialize;

/**
 * The trait of a class that can produce a builder (as in the pattern) from its properties.
 * <br/>
 * The main stipulation is that the produced builder is {@link Buildable} and,
 * assuming {@code toBuilderable} to be an instance of a {@code ToBuilderable}, then it is always true that:
 *
 * <pre>
 * {@code
 * toBuilderable.toBuilder().build().equals(toBuilderable);
 * }
 * </pre>
 * <br/>
 * The motivation for this type and {@link Buildable} is to streamline the interaction of Lombok and Jackson when
 * using immutable DTOs. By making the DTOs implement this interface, and their (generated) builders {@link Buildable},
 * we can obtain a modifiable version of a DTO that is completely equivalent to it, and that can be updated and built
 * back to the original DTO type with very little effort.
 *
 * @param <T> The type of the builder produced by this trait.
 */
public interface ToBuilderable<T extends Buildable<? extends ToBuilderable<T>>> {
    /**
     * Convert this instance into a builder with properties equal to this.
     *
     * @return a primed builder for {@code T}
     */
    default T toBuilder() {
        return null;
    }
}
