package me.ap.challenge.widgetapp.core.service;

import me.ap.challenge.widgetapp.core.model.Widget;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Implements the actual persistence of {@link Widget}s.
 */
@Component
public class WidgetStorage {
    private final ConcurrentHashMap<Long, Widget> widgetDb = new ConcurrentHashMap<>();
    private final ConcurrentSkipListMap<Integer, Widget> zIndex = new ConcurrentSkipListMap<>();

    public synchronized Widget store(Widget widget) {
        widgetDb.put(widget.getId(), widget);
        zIndex.put(widget.getZ(), widget);

        return widget;
    }

    public synchronized Optional<Widget> removeByZ(Integer z) {
        var widget = zIndex.remove(z);

        if (widget != null) {
            widgetDb.remove(widget.getId());
        }

        return Optional.ofNullable(widget);
    }

    public synchronized Optional<Widget> removeById(Long id) {
        var widget = widgetDb.remove(id);

        if (widget != null) {
            zIndex.remove(widget.getZ());
        }

        return Optional.ofNullable(widget);
    }

    /**
     * Returns all widgets stored, sorted by Z ASC.
     *
     * @return all widgets
     */
    public Collection<Widget> getAll() {
        return zIndex.values();
    }

    public Optional<Widget> getById(Long id) {
        return Optional.ofNullable(widgetDb.get(id));
    }

    public Optional<Widget> getByZ(Integer z) {
        return Optional.ofNullable(zIndex.get(z));
    }

    public Optional<Widget> ceilingByZ(Integer z) {
        var ceilingEntry = zIndex.ceilingEntry(z);

        if (ceilingEntry == null) {
            return Optional.empty();
        } else {
            return Optional.of(ceilingEntry.getValue());
        }
    }

    public List<Integer> keysGreaterOrEqualByZDesc(int z) {
        return new ArrayList<>(zIndex.tailMap(z).descendingMap().keySet());
    }

    public Optional<Integer> getMaxZ() {
        if (zIndex.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(zIndex.lastKey());
        }
    }
}
