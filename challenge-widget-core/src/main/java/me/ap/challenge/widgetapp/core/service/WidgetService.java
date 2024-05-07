package me.ap.challenge.widgetapp.core.service;

import me.ap.challenge.widgetapp.core.model.Widget;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;

/**
 * Business logic for {@link Widget}s.
 */
@Component
public class WidgetService {
    private final WidgetStorage storage;
    private final Random random = new Random();

    public WidgetService(WidgetStorage storage) {
        this.storage = storage;
    }

    /**
     * Retrieves a {@link Widget} by its id, if present.
     *
     * @param id the id of the desired Widget
     * @return the desired Widget, if present
     */
    public Optional<Widget> findById(Long id) {
        return storage.findById(id);
    }

    /**
     * Retrieves a {@link Widget} by its id.
     *
     * @param id the id of the desired Widget
     * @return the desired Widget
     * @throws NoSuchElementException if the desired Widget does not exist
     */
    public Widget getById(Long id) {
        return findById(id).orElseThrow(NoSuchElementException::new);
    }

    /**
     * Lists all stored {@link Widget}s.
     *
     * @return a collection containing all stored Widgets
     */
    public Collection<Widget> getAll() {
        return storage.findAll();
    }

    /**
     * Computes the maximum {@code Z} among all stored {@link Widget}s.
     *
     * @return the maximum {@code Z}, if any Widget exists
     */
    public Optional<Integer> getMaxZ() {
        return storage.getMaxZ();
    }

    /**
     * Creates a {@link Widget} like the one in argument.
     * <p>
     * The creation might entail changing some of the properties of the given Widget
     * and/or of some other already in the persistence layer.
     * <p>
     * The new Widget will have a newly generated id.
     * <p>
     * The method is not synchronized, it delegates thread safety to the mutator.
     *
     * @param widget the new Widget desired state
     * @return the actual new Widget state
     */
    public Widget create(Widget widget) {
        return storage.save(new Widget(random.nextLong(), widget.getWidth(), widget.getHeight(), ensureZ(widget)));
    }

    private synchronized Integer ensureZ(Widget widget) {
        if (widget.getZ() == null) {
            return getMaxZ().orElse(0) + 1;
        } else {
            makeSpaceForZ(widget.getZ());
            return widget.getZ();
        }
    }

    /**
     * Deletes a {@link Widget} by id.
     * <p>
     * This operation does not fail if no Widget exists with the given id.
     *
     * @param id the id of the Widget to delete
     */
    public void delete(Long id) {
        storage.deleteById(id);
    }

    /**
     * Updates the given stored {@link Widget} state with the desired new one.
     * <p>
     * As with {@link #create(Widget)}, the new actual state may differ from the desired one
     * and/or other Widgets might have been modified.
     * <p>
     * The method is synchronized as a gross measure to avoid race conditions accessing the set of Zs.
     *
     * @param original the currently stored state
     * @param updated  the new desired state
     * @return the actual new state
     */
    public synchronized Widget update(Widget original,
                                      Widget updated) {
        updated.setId(original.getId());

        // if the z index need change, do it
        if (!original.getZ().equals(updated.getZ())) {
            storage.deleteById(updated.getId());
            makeSpaceForZ(updated.getZ());
        }

        return storage.save(updated);
    }

    /**
     * Updates the given stored {@link Widget} state, identified by id, with the desired new one.
     *
     * @param id       the id of the Widget to update
     * @param newState the new Widget state
     * @return the actual new state
     * @see #update(Widget, Widget) for more context
     */
    public Widget update(Long id,
                         Widget newState) {
        return update(getById(id), newState);
    }

    /**
     * Ensures the storage layer has no {@link Widget} with the given {@code Z} by rearranging the existing Widgets.
     *
     * @param z the Z to free up
     */
    private void makeSpaceForZ(int z) {
        // find if the new widget Z already exists
        if (storage.findByZ(z).isPresent()) {
            // move widgets from Z to inf by 1
            for (Integer currentZ : storage.keysGreaterOrEqualByZDesc(z)) {
                var updatedWidget = storage.deleteByZ(currentZ);
                if (updatedWidget.isPresent()) {
                    updatedWidget.get().setZ(currentZ + 1);
                    storage.save(updatedWidget.get());
                }
            }
        }
    }
}
