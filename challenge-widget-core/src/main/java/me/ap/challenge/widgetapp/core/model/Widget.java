package me.ap.challenge.widgetapp.core.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;


/**
 * Models a Widget and acts as a DTO.
 */
@Builder(toBuilder = true)
public record Widget(
        Long id,
        @NotNull Integer width,
        @NotNull Integer height,
        Integer z
) {
}