package me.ap.challenge.widgetapp.core.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WidgetTest {

    private final Widget widget = new Widget(1L, 2, 3, 4);

    @Test
    void baseToBuilderableContract() {
        assertEquals(widget, widget.toBuilder().build());
    }

    @Test
    void builderModifiesPreviousProperties() {
        assertEquals(33, widget.toBuilder().z(33).build().z());
    }
}