package me.ap.challenge.widgetapp.core.service;

import me.ap.challenge.widgetapp.core.model.Widget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WidgetStorageTest {
    private WidgetStorage storage;

    @BeforeEach
    public void setUp() {
        storage = new WidgetStorage();
    }

    @Test
    public void testGetById() {
        storage.save(Widget.builder().id(33L).height(1).width(2).z(-3).build());

        var maybeWidget = storage.findById(33L);
        assertTrue(maybeWidget.isPresent());
        var widget = maybeWidget.get();
        assertEquals(Long.valueOf(33), widget.id());
        assertEquals(Integer.valueOf(1), widget.height());
        assertEquals(Integer.valueOf(2), widget.width());
        assertEquals(Integer.valueOf(-3), widget.z());
    }

    @Test
    public void testGetAllIsSorted() {
        storage.save(Widget.builder().id(1L).height(11).width(12).z(13).build());
        storage.save(Widget.builder().id(2L).height(21).width(22).z(-23).build());
        storage.save(Widget.builder().id(3L).height(31).width(32).z(33).build());

        var allWidgetsSorted = storage.findAll().stream().map(Widget::z).collect(Collectors.toList());
        assertEquals(List.of(-23, 13, 33), allWidgetsSorted);
    }

    @Test
    public void testCeilingByZ() {
        storage.save(Widget.builder().id(1L).height(11).width(12).z(1).build());
        var widget3 = storage.save(Widget.builder().id(2L).height(21).width(22).z(3).build());
        storage.save(Widget.builder().id(3L).height(31).width(32).z(4).build());

        var ceiling2 = storage.ceilingByZ(2);
        assertTrue(ceiling2.isPresent());
        assertEquals(widget3.id(), ceiling2.get().id());
    }
}