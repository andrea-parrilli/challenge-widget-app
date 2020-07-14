package me.ap.challenge.widgetapp.core.service;

import me.ap.challenge.widgetapp.core.model.Widget;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Due to the tight coupling between {@link WidgetPersistence} and {@link WidgetStorage},
 * these tests will involve a real instance of the storage engine.
 * <p>
 * Main reason is to avoid mocking complex methods of the storage engine.
 * If the storage engine would become pluggable, then these tests must be refined and concern the persistence layer only.
 */
public class WidgetPersistenceTest {
    private WidgetPersistence persistence;

    @Before
    public void setUp() {
        persistence = new WidgetPersistence(new WidgetStorage());
    }

    @Test
    public void testIdIsAssigned() {
        var widget = persistence.create(Widget.builder().id(1L).height(11).width(12).z(1).build());
        assertNotNull(widget.getId());
        assertNotEquals(1, widget.getId());
    }

    @Test
    public void testStoreRearrangesByZ() {
        var widget1 = persistence.create(Widget.builder().id(1L).height(11).width(12).z(1).build());
        var widget2 = persistence.create(Widget.builder().id(2L).height(21).width(22).z(2).build());
        var widget3 = persistence.create(Widget.builder().id(3L).height(31).width(32).z(1).build());

        assertEquals(Integer.valueOf(2), widget1.getZ());
        assertEquals(Integer.valueOf(3), widget2.getZ());
        assertEquals(Integer.valueOf(1), widget3.getZ());
    }
}