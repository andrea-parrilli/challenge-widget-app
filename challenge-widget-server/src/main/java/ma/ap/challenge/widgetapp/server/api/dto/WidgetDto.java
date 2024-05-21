package ma.ap.challenge.widgetapp.server.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import me.ap.challenge.widgetapp.core.model.Widget;
import me.ap.tools.lang.builder.Buildable;
import me.ap.tools.lang.builder.ToBuilderable;


/**
 * DTO for {@link Widget}.
 */
@Builder(toBuilder = true)
public record WidgetDto(
        Long id,
        @NotNull Integer width,
        @NotNull Integer height,
        Integer z
) implements ToBuilderable<WidgetDto.WidgetDtoBuilder> {
    public static class WidgetDtoBuilder implements Buildable<WidgetDto> {
    }
}