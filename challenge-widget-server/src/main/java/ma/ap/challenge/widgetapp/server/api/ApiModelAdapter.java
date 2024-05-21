package ma.ap.challenge.widgetapp.server.api;

import lombok.AllArgsConstructor;
import ma.ap.challenge.widgetapp.server.api.dto.WidgetDto;
import me.ap.challenge.widgetapp.core.model.Widget;
import me.ap.challenge.widgetapp.core.service.WidgetService;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

/**
 * Adapts the web API DTOs to the internal model and services.
 * <p>
 * Uses delegation to implement an adapter to {@link WidgetService}.
 */
@Component
@AllArgsConstructor
public class ApiModelAdapter {
    private final WidgetService widgetService;

    public Optional<WidgetDto> findById(Long id) {
        return widgetService.findById(id).map(this::toDto);
    }

    public WidgetDto getById(Long id) {
        return widgetService.findById(id).map(this::toDto).orElseThrow(
                () -> new IllegalArgumentException(String.format("Widget(%d) does not exist", id))
        );
    }

    public Collection<WidgetDto> getAll() {
        return widgetService.getAll().stream().map(this::toDto).toList();
    }

    public WidgetDto create(WidgetDto widgetDto) {
        return toDto(widgetService.create(toModel(widgetDto)));
    }

    public void delete(Long id) {
        widgetService.delete(id);
    }

    public WidgetDto update(WidgetDto original, WidgetDto modified) {
        // Make sure the id is not changed, otherwise a new entity could be created
        if (!original.id().equals(modified.id())) {
            throw new IllegalArgumentException("It is not allowed to modify the Widget id");
        }

        return toDto(widgetService.update(toModel(original), toModel(modified)));
    }

    WidgetDto toDto(Widget widget) {
        return new WidgetDto(
                widget.id(),
                widget.width(),
                widget.height(),
                widget.z());
    }

    Widget toModel(WidgetDto widgetDto) {
        return new Widget(
                widgetDto.id(),
                widgetDto.width(),
                widgetDto.height(),
                widgetDto.z());
    }
}
