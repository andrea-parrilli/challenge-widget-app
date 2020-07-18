package me.ap.challenge.widgetapp.core.service;

import me.ap.challenge.widgetapp.core.model.Widget;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Business logic for {@link Widget}s.
 */
@Component
public class WidgetService {
    private final WidgetPersistence persistence;

    public WidgetService(WidgetPersistence persistence) {
        this.persistence = persistence;
    }

    public Optional<Widget> findById(Long id) {
        return persistence.getById(id);
    }

    public Widget getById(Long id) {
        return persistence.getById(id).orElseThrow(NoSuchElementException::new);
    }

    public Collection<Widget> getAll() {
        return persistence.getAll();
    }

    public Widget create(Widget widget) {
        if (widget.getZ() == null) {
            widget.setZ(persistence.getMaxZ().orElse(0) + 1);
        }

        return persistence.create(widget);
    }

    /**
     * Deletes a {@link Widget} by id.
     * <p>
     * This method does not fail if the id does not exist.
     *
     * @param id the id of the Widget to delete
     */
    public void delete(Long id) {
        persistence.delete(id);
    }

    public Widget update(Long id, Widget widget) {
        return update(getById(id), widget);
    }

    public Widget update(Widget original, Widget updated) {
        updated.setId(original.getId());

        return persistence.update(updated, original);
    }
}
