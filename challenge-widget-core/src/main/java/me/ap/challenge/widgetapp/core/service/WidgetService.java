package me.ap.challenge.widgetapp.core.service;

import jakarta.transaction.Transactional;
import me.ap.challenge.widgetapp.core.model.Widget;
import me.ap.challenge.widgetapp.core.repo.WidgetRepo;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Business logic for {@link Widget}s.
 */
@Component
public class WidgetService {
    private final WidgetRepo widgetRepo;

    public WidgetService(WidgetRepo widgetRepo) {
        this.widgetRepo = widgetRepo;
    }

    /**
     * Retrieves a {@link Widget} by its id, if present.
     *
     * @param id the id of the desired Widget
     * @return the desired Widget, if present
     */
    public Optional<Widget> findById(Long id) {
        return widgetRepo.findById(id);
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
        return widgetRepo.findAll();
    }

    /**
     * Computes the maximum {@code Z} among all stored {@link Widget}s.
     *
     * @return the maximum {@code Z}, if any Widget exists
     */
    public Optional<Integer> getMaxZ() {
        return widgetRepo.findMaxZ().map(Widget::z);
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
    @Transactional
    public Widget create(Widget widget) {
        return widgetRepo.save(ensureZ(widget));
    }

    @Transactional
    protected Widget ensureZ(Widget widget) {
        if (widget.z() == null) {
            return widget.z(getMaxZ().orElse(0) + 1);
        } else {
            makeSpaceForZ(widget.z());
            return widget;
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
        widgetRepo.deleteById(id);
    }

    /**
     * Updates the given stored {@link Widget} state with the desired new one.
     * <p>
     * As with {@link #create(Widget)}, the new actual state may differ from the desired one
     * and/or other Widgets might have been modified.
     *
     * @param original the currently stored state
     * @param updated  the new desired state
     * @return the actual new state
     */
    @Transactional
    public Widget update(Widget original,
                         Widget updated) {
        // if the z index need change, remove old widget and make space for new Z
        if (!original.z().equals(updated.z())) {
            widgetRepo.deleteById(original.id());
            makeSpaceForZ(updated.z());
        }

        return widgetRepo.save(updated);
    }

    /**
     * Ensures the storage layer has no {@link Widget} with the given {@code Z} by rearranging the existing Widgets.
     *
     * @param z the Z to free up
     */
    @Transactional
    private void makeSpaceForZ(int z) {
        // find if the new widget Z already exists
        if (widgetRepo.existsWidgetByZ(z)) {
            // move widgets from Z to inf by 1
            widgetRepo.shiftZbyOne(z);
        }
    }
}
