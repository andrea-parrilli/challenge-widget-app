package ma.ap.challenge.widgetapp.server.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import ma.ap.challenge.widgetapp.server.ApiPaths;
import me.ap.challenge.widgetapp.core.model.Widget;
import me.ap.challenge.widgetapp.core.service.WidgetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Collection;
import java.util.NoSuchElementException;

@RestController
@RequestMapping(ApiPaths.PATH_WIDGET)
@AllArgsConstructor
public class WidgetController {
    private final WidgetService widgetService;
    private final ObjectMapper mapper;

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Widget getWidget(@PathVariable Long id) {
        return widgetService.findById(id).orElseThrow(NoSuchElementException::new);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<Widget> getAll() {
        return widgetService.getAll();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Widget create(@Valid @RequestBody Widget widget) {
        if (widget.getId() != null) {
            throw new IllegalArgumentException("It is not allowed to create a Widget with id: the id is autogenerated");
        }

        return widgetService.create(widget);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        widgetService.delete(id);
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public Widget update(@PathVariable Long id, @Valid @RequestBody Widget widget) {
        if (widget.getId() != null) {
            throw new IllegalArgumentException("It is not allowed to modify the Widget id");
        }

        return widgetService.update(id, widget);
    }

    @PatchMapping(value = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Widget patch(@PathVariable Long id, HttpServletRequest request) throws IOException {
        var original = widgetService.getById(id);
        // get a clone of the original: the business logic layer is responsible to actually change the visible state of the Widget
        var updated = original.toBuilder().build();

        mapper.readerForUpdating(updated).readValue(request.getInputStream());

        return widgetService.update(original, updated);
    }
}
