package me.ap.challenge.widgetapp.core.service;

import me.ap.challenge.widgetapp.core.model.Widget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Due to the tight coupling between {@link WidgetService} and {@link WidgetStorage},
 * these tests will involve a real instance of the storage engine.
 * <p>
 * Main reason is to avoid mocking complex methods of the storage engine.
 * If the storage engine became pluggable, then these tests needed to be refined and concern the persistence layer only.
 */
public class WidgetServiceTest {
    private WidgetService service;

    @BeforeEach
    public void setUp() {
        service = new WidgetService(new WidgetStorage());
    }

    @Test
    public void testIdIsAssigned() {
        var widget = service.create(Widget.builder().id(1L).height(11).width(12).z(1).build());
        assertNotNull(widget.id());
        assertNotEquals(1, widget.id());
    }

    @Test
    public void testStoreRearrangesByZ() {
        var widget1 = service.create(Widget.builder().id(1L).height(11).width(12).z(1).build());
        var widget2 = service.create(Widget.builder().id(2L).height(21).width(22).z(2).build());
        var widget3 = service.create(Widget.builder().id(3L).height(31).width(32).z(1).build());

        assertEquals(Integer.valueOf(2), service.getById(widget1.id()).z());
        assertEquals(Integer.valueOf(3), service.getById(widget2.id()).z());
        assertEquals(Integer.valueOf(1), service.getById(widget3.id()).z());
    }

    @Test
    public void testUpdateSameZ() {
        var original = service.create(Widget.builder().id(1L).height(11).width(12).z(1).build());
        var newState = Widget.builder().height(22).width(24).z(1).build();

        var updatedWidget = service.update(original, newState);

        assertEquals(Integer.valueOf(22), updatedWidget.height());
        assertEquals(Integer.valueOf(24), updatedWidget.width());
        assertEquals(Integer.valueOf(1), updatedWidget.z());
        assertEquals(1, service.getAll().size());
    }

    @Test
    public void testUpdateNewZNotRearranged() {
        var widget1 = service.create(Widget.builder().height(11).width(12).z(1).build());
        var widget2 = service.create(Widget.builder().height(21).width(22).z(3).build());
        var newState1 = Widget.builder().height(22).width(24).z(2).build();

        var updatedWidget = service.update(widget1, newState1);

        assertEquals(2, service.getAll().size());

        assertEquals(widget1.id(), updatedWidget.id());
        assertEquals(Integer.valueOf(22), updatedWidget.height());
        assertEquals(Integer.valueOf(24), updatedWidget.width());
        assertEquals(Integer.valueOf(2), updatedWidget.z());

        assertEquals(Integer.valueOf(3), service.getById(widget2.id()).z());
    }

    @Test
    public void testUpdateNewZRearranged() {
        var widget1 = service.create(Widget.builder().height(11).width(12).z(1).build());
        var widget2 = service.create(Widget.builder().height(21).width(22).z(2).build());
        var newState1 = Widget.builder().height(22).width(24).z(2).build();

        var updatedWidget = service.update(widget1, newState1);

        assertEquals(2, service.getAll().size());

        assertEquals(widget1.id(), updatedWidget.id());
        assertEquals(Integer.valueOf(22), updatedWidget.height());
        assertEquals(Integer.valueOf(24), updatedWidget.width());
        assertEquals(Integer.valueOf(2), updatedWidget.z());

        assertEquals(Integer.valueOf(3), service.getById(widget2.id()).z());
    }
}
