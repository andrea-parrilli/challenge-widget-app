package me.ap.challenge.widgetapp.core.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FlufferTest {
    @Test
    void testBuilder() {
        var fluffer = Fluffer.builder().num(1).str("miao").build();
        assertEquals(1, fluffer.num());
    }

    @Test
    void testSerialize() {
        
    }
}