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
                .map(Widget::id)
                .forEach(widgetService::delete);
    }

    @Test
    void getWidget() {
        var widget = widgetService.create(Widget.builder().width(1).height(2).z(3).build());

        api.get().uri(PATH_WIDGET + widget.id())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("id").isEqualTo(widget.id())
                .jsonPath("width").isEqualTo(widget.width())
                .jsonPath("height").isEqualTo(widget.height())
                .jsonPath("z").isEqualTo(widget.z());
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
                    assertEquals(Set.of(widget1.id(), widget2.id()),
                                 result.getResponseBody().stream()
                                         .map(Widget::id)
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
                .jsonPath("width").isEqualTo(widget.width())
                .jsonPath("height").isEqualTo(widget.height())
                .jsonPath("z").isEqualTo(widget.z());

        var found = widgetService.findById(id.get());
        assertTrue(found.isPresent());
        assertEquals(widget.height(), found.get().height());
        assertEquals(widget.width(), found.get().width());
        assertEquals(widget.z(), found.get().z());
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

        api.delete().uri(PATH_WIDGET + widget.id())
                .exchange()
                .expectStatus().isNoContent()
                .expectBody()
                .isEmpty();

        assertFalse(widgetService.findById(widget.id()).isPresent());

        // test delete is reentrant
        api.delete().uri(PATH_WIDGET + widget.id())
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void update() {
        var original = widgetService.create(Widget.builder().width(1).height(2).z(3).build());

        // update just width
        var request = original.toBuilder().id(null).width(11).build();

        api.put().uri(PATH_WIDGET + original.id())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("id").isEqualTo(original.id())
                .jsonPath("width").isEqualTo(11)
                .jsonPath("height").isEqualTo(original.height())
                .jsonPath("z").isEqualTo(original.z());

        var found = widgetService.findById(original.id());
        assertTrue(found.isPresent());
        assertEquals(11, found.get().width());
        assertEquals(original.height(), found.get().height());
        assertEquals(original.z(), found.get().z());
    }

    @Test
    void patch() {
        var original = widgetService.create(Widget.builder().width(1).height(2).z(3).build());

        // update just width
        var request = Map.of("width", 11);

        api.patch().uri(PATH_WIDGET + original.id())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("id").isEqualTo(original.id())
                .jsonPath("width").isEqualTo(11)
                .jsonPath("height").isEqualTo(original.height())
                .jsonPath("z").isEqualTo(original.z());

        var found = widgetService.findById(original.id());
        assertTrue(found.isPresent());
        assertEquals(11, found.get().width());
        assertEquals(original.height(), found.get().height());
        assertEquals(original.z(), found.get().z());
    }
}