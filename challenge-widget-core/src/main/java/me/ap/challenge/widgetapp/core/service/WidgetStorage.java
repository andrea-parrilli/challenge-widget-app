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
 * <p>
 * Exposes an API akin to a JPA repository.
 */
@Component
public class WidgetStorage {
    private final ConcurrentHashMap<Long, Widget> widgetDb = new ConcurrentHashMap<>();
    private final ConcurrentSkipListMap<Integer, Widget> zIndex = new ConcurrentSkipListMap<>();

    public synchronized Widget save(Widget widget) {
        zIndex.put(widget.getZ(), widget);
        widgetDb.put(widget.getId(), widget);

        return widget;
    }

    public synchronized Optional<Widget> deleteByZ(Integer z) {
        var widget = zIndex.remove(z);

        if (widget != null) {
            widgetDb.remove(widget.getId());
        }

        return Optional.ofNullable(widget);
    }

    public synchronized Optional<Widget> deleteById(Long id) {
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
    public Collection<Widget> findAll() {
        return zIndex.values();
    }

    public Optional<Widget> findById(Long id) {
        return Optional.ofNullable(widgetDb.get(id));
    }

    public Optional<Widget> findByZ(Integer z) {
        return Optional.ofNullable(zIndex.get(z));
    }

    /**
     * Finds the {@link Widget} with the least {@code Z} greater than or equal to the given Z, if present.
     *
     * @param z the min Z
     * @return the widget with the least Z greater than or equal to the given Z, if present
     */
    public Optional<Widget> ceilingByZ(Integer z) {
        var ceilingEntry = zIndex.ceilingEntry(z);

        if (ceilingEntry == null) {
            return Optional.empty();
        } else {
            return Optional.of(ceilingEntry.getValue());
        }
    }

    /**
     * Lists all used {@code Z} values greater than or equal to the given Z in descending order.
     *
     * @param z then min Z
     * @return the lists all used {@code Z} values greater than or equal to the given Z in descending order.
     */
    public List<Integer> keysGreaterOrEqualByZDesc(int z) {
        return new ArrayList<>(zIndex.tailMap(z).descendingMap().keySet());
    }

    /**
     * Finds the maximum {@code Z} stored, if any {@link Widget} is present.
     *
     * @return the max Z, if any {@link Widget} is present
     */
    public Optional<Integer> getMaxZ() {
        if (zIndex.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(zIndex.lastKey());
        }
    }
}
