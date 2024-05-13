package me.ap.challenge.widgetapp.core.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import me.ap.challenge.widgetapp.core.deserialize.Buildable;
import me.ap.challenge.widgetapp.core.deserialize.ToBuilderable;


/**
 * Models a Widget and acts as a DTO.
 */
@Builder(toBuilder = true)
public record Widget (
        Long id,
        @NotNull Integer width,
        @NotNull Integer height,
        Integer z
) implements ToBuilderable<Widget.WidgetBuilder> {
    public static class WidgetBuilder implements Buildable<Widget> {}
}