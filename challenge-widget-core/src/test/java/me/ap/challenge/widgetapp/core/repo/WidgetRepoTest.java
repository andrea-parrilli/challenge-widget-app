package me.ap.challenge.widgetapp.core.repo;

import me.ap.challenge.widgetapp.core.WidgetAppCoreTestConfiguration;
import me.ap.challenge.widgetapp.core.model.Widget;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@ContextConfiguration(classes = WidgetAppCoreTestConfiguration.class)
public class WidgetRepoTest {
    @Autowired
    private WidgetRepo repo;

    @Test
    void createGet() {
        var newWidget = repo.save(Widget.builder().height(1).width(2).z(-3).build());
        assertNotNull(newWidget.id());

        var widget = repo.findById(newWidget.id())
                .orElseThrow(() -> new AssertionError("Created Widget cannot be found"));

        assertEquals(newWidget.id(), widget.id());
        assertEquals(1, widget.height());
        assertEquals(2, widget.width());
        assertEquals(-3, widget.z());
    }
}