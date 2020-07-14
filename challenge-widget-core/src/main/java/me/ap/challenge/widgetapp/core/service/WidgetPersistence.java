package me.ap.challenge.widgetapp.core.service;

import me.ap.challenge.widgetapp.core.model.Widget;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;
import java.util.Random;

/**
 * Abstracts the access to the persistence layer for {@link Widget}s.
 */
@Component
public class WidgetPersistence {
    private final WidgetStorage storage;
    private final Random random = new Random();

    public WidgetPersistence(WidgetStorage storage) {
        this.storage = storage;
    }

    public Optional<Widget> getById(Long id) {
        return storage.getById(id);
    }

    public Collection<Widget> getAll() {
        return storage.getAll();
    }

    public Optional<Integer> getMaxZ() {
        return storage.getMaxZ();
    }

    public Widget create(Widget widget) {
        // find if the new widget Z already exists
        makeSpaceForZ(widget.getZ());
        widget.setId(random.nextLong());

        return storage.store(widget);
    }

    public Optional<Widget> delete(Long id) {
        return storage.removeById(id);
    }

    public synchronized Widget update(Widget newState, Widget prevState) {
        // if the z index need change, do it
        if (!newState.getZ().equals(prevState.getZ())) {
            makeSpaceForZ(newState.getZ());
            storage.removeById(newState.getId());
        }

        return storage.store(newState);
    }

    private void makeSpaceForZ(int z) {
        // find if the new widget Z already exists
        if (storage.getByZ(z).isPresent()) {
            // move widgets from Z to inf by 1
            for (Integer currentZ : storage.keysGreaterOrEqualByZDesc(z)) {
                var updatedWidget = storage.removeByZ(currentZ);
                if (updatedWidget.isPresent()) {
                    updatedWidget.get().setZ(currentZ + 1);
                    storage.store(updatedWidget.get());
                }
            }
        }
    }
}
