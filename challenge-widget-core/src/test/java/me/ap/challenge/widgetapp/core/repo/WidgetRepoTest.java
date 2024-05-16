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

    @Test
    public void testGetAllIsSorted() {
        repo.save(Widget.builder().id(1L).height(11).width(12).z(13).build());
        repo.save(Widget.builder().id(2L).height(21).width(22).z(-23).build());
        repo.save(Widget.builder().id(3L).height(31).width(32).z(33).build());

        var allWidgetsSorted = repo.findAll().stream().map(Widget::z).collect(Collectors.toList());
        assertEquals(List.of(-23, 13, 33), allWidgetsSorted);
    }
}