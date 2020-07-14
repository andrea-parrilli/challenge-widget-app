package me.ap.challenge.widgetapp.core.service;

import me.ap.challenge.widgetapp.core.model.Widget;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;
import java.util.Random;

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

    public Widget create(Widget widget) {
        // find if the new widget Z already exists
        if (storage.ceilingByZ(widget.getZ()).isPresent()) {
            // move all widgets from Z to inf by one
            for (Integer z : storage.keysGreaterOrEqualByZDesc(widget.getZ())) {
                var updatedWidget = storage.removeByZ(z);
                updatedWidget.ifPresent(w -> w.setZ(w.getZ() + 1));
                storage.store(updatedWidget.get());
            }
        }
        widget.setId(random.nextLong());
        return storage.store(widget);
    }
}
