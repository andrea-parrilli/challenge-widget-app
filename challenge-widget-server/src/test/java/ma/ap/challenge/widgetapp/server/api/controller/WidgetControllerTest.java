package ma.ap.challenge.widgetapp.server.api.controller;

import ma.ap.challenge.widgetapp.server.WidgetAppServer;
import me.ap.challenge.widgetapp.core.model.Widget;
import me.ap.challenge.widgetapp.core.service.WidgetService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static ma.ap.challenge.widgetapp.server.ApiPaths.PATH_WIDGET;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        classes = {WidgetAppServer.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class WidgetControllerTest {
    @Autowired
    private WebTestClient api;
    @Autowired
    private WidgetService widgetService;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
        widgetService.getAll().stream()
                .map(Widget::getId)
                .forEach(widgetService::delete);
    }

    @Test
    void getWidget() {
        var widget = widgetService.create(Widget.builder().width(1).height(2).z(3).build());

        api.get().uri(PATH_WIDGET + widget.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("id").isEqualTo(widget.getId())
                .jsonPath("width").isEqualTo(widget.getWidth())
                .jsonPath("height").isEqualTo(widget.getHeight())
                .jsonPath("z").isEqualTo(widget.getZ());
    }

    @Test
    void getAll() {
        var widget1 = widgetService.create(Widget.builder().width(1).height(2).z(3).build());
        var widget2 = widgetService.create(Widget.builder().width(11).height(22).z(33).build());

        api.get().uri(PATH_WIDGET)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Widget.class)
                .hasSize(2)
                .consumeWith(result -> {
                    assertEquals(Set.of(widget1.getId(), widget2.getId()),
                                 result.getResponseBody().stream()
                                         .map(Widget::getId)
                                         .collect(Collectors.toSet())
                    );
                });
    }

    @Test
    void create() {
        var widget = Widget.builder().width(1).height(2).z(3).build();

        var id = new AtomicReference<Long>();
        api.post().uri(PATH_WIDGET)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(widget)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("id").isNotEmpty()
                .jsonPath("id").value(id::set)
                .jsonPath("width").isEqualTo(widget.getWidth())
                .jsonPath("height").isEqualTo(widget.getHeight())
                .jsonPath("z").isEqualTo(widget.getZ());

        var found = widgetService.findById(id.get());
        assertTrue(found.isPresent());
        assertEquals(widget.getHeight(), found.get().getHeight());
        assertEquals(widget.getWidth(), found.get().getWidth());
        assertEquals(widget.getZ(), found.get().getZ());
    }

    @Test
    void createValidates() {
        var widget = Widget.builder().width(1).z(3).build();

        var id = new AtomicReference<Long>();
        api.post().uri(PATH_WIDGET)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(widget)
                .exchange()
                .expectStatus().isBadRequest();

        assertTrue(widgetService.getAll().isEmpty());
    }

    @Test
    void delete() {
        var widget = widgetService.create(Widget.builder().width(1).height(2).z(3).build());

        api.delete().uri(PATH_WIDGET + widget.getId())
                .exchange()
                .expectStatus().isNoContent()
                .expectBody()
                .isEmpty();

        assertFalse(widgetService.findById(widget.getId()).isPresent());

        // test delete is reentrant
        api.delete().uri(PATH_WIDGET + widget.getId())
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void update() {
        var original = widgetService.create(Widget.builder().width(1).height(2).z(3).build());

        // update just width
        var request = original.toBuilder().id(null).width(11).build();

        api.put().uri(PATH_WIDGET + original.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("id").isEqualTo(original.getId())
                .jsonPath("width").isEqualTo(11)
                .jsonPath("height").isEqualTo(original.getHeight())
                .jsonPath("z").isEqualTo(original.getZ());

        var found = widgetService.findById(original.getId());
        assertTrue(found.isPresent());
        assertEquals(11, found.get().getWidth());
        assertEquals(original.getHeight(), found.get().getHeight());
        assertEquals(original.getZ(), found.get().getZ());
    }

    @Test
    void patch() {
        var original = widgetService.create(Widget.builder().width(1).height(2).z(3).build());

        // update just width
        var request = Map.of("width", 11);

        api.patch().uri(PATH_WIDGET + original.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("id").isEqualTo(original.getId())
                .jsonPath("width").isEqualTo(11)
                .jsonPath("height").isEqualTo(original.getHeight())
                .jsonPath("z").isEqualTo(original.getZ());

        var found = widgetService.findById(original.getId());
        assertTrue(found.isPresent());
        assertEquals(11, found.get().getWidth());
        assertEquals(original.getHeight(), found.get().getHeight());
        assertEquals(original.getZ(), found.get().getZ());
    }
}