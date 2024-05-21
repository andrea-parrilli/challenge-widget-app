package ma.ap.challenge.widgetapp.server.api.controller;

import ma.ap.challenge.widgetapp.server.WidgetAppServer;
import ma.ap.challenge.widgetapp.server.api.ApiModelAdapter;
import ma.ap.challenge.widgetapp.server.api.dto.WidgetDto;
import me.ap.challenge.widgetapp.core.WidgetAppCoreConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ma.ap.challenge.widgetapp.server.ApiPaths.PATH_WIDGET;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        classes = {WidgetAppServer.class, WidgetAppCoreConfiguration.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureWebTestClient(timeout = "30000000")
class WidgetControllerTest {
    private final WidgetDto widgetDto1 = WidgetDto.builder().width(1).height(2).z(3).build();
    @Autowired
    private WebTestClient api;
    @Autowired
    private ApiModelAdapter model;

    @AfterEach
    void tearDown() {
        model.getAll().stream()
                .map(WidgetDto::id)
                .forEach(model::delete);
    }

    @Test
    void getWidget() {
        var widget = model.create(widgetDto1);

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
        var widgets = Stream.of(widgetDto1, WidgetDto.builder().width(11).height(22).z(33).build())
                .map(model::create)
                .collect(Collectors.toUnmodifiableSet());

        api.get().uri(PATH_WIDGET)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(WidgetDto.class)
                .hasSize(2)
                .consumeWith(result -> assertEquals(
                        widgets,
                        result.getResponseBody().stream().collect(Collectors.toUnmodifiableSet())));
    }

    @Test
    void create() {
        var id = new AtomicReference<Long>();
        api.post().uri(PATH_WIDGET)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(widgetDto1)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("id").isNotEmpty()
                .jsonPath("id").value(v -> id.set(Long.valueOf((Integer) v)))
                .jsonPath("width").isEqualTo(widgetDto1.width())
                .jsonPath("height").isEqualTo(widgetDto1.height())
                .jsonPath("z").isEqualTo(widgetDto1.z());

        var found = model.findById(id.get());
        assertTrue(found.isPresent());
        assertEquals(widgetDto1.height(), found.get().height());
        assertEquals(widgetDto1.width(), found.get().width());
        assertEquals(widgetDto1.z(), found.get().z());
    }

    @Test
    void createValidates() {
        api.post().uri(PATH_WIDGET)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(WidgetDto.builder().z(33).build())
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("message").value(containsString("height"))
                .jsonPath("message").value(containsString("width"))
                .jsonPath("message").value(containsString("null"));

        assertTrue(model.getAll().isEmpty());
    }

    @Test
    void delete() {
        var widget = model.create(widgetDto1);

        api.delete().uri(PATH_WIDGET + widget.id())
                .exchange()
                .expectStatus().isNoContent()
                .expectBody()
                .isEmpty();

        assertFalse(model.findById(widget.id()).isPresent());

        // test delete is reentrant
        api.delete().uri(PATH_WIDGET + widget.id())
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void putReplaces() {
        var original = model.create(widgetDto1);

        // update just width
        var request = original.toBuilder().width(33).build();

        api.put().uri(PATH_WIDGET + original.id())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("id").exists()
                .jsonPath("id").isEqualTo(original.id())
                .jsonPath("width").isEqualTo(33)
                .jsonPath("height").isEqualTo(widgetDto1.height())
                .jsonPath("z").isEqualTo(widgetDto1.z());

        var found = model.findById(original.id());
        assertTrue(found.isPresent());
        assertEquals(33, found.get().width());
        assertEquals(original.height(), found.get().height());
        assertEquals(original.z(), found.get().z());
    }

    @Test
    void putValidates() {
        var original = model.create(widgetDto1);

        // Make invalid: missing height
        var request = WidgetDto.builder().height(1).build();

        api.put().uri(PATH_WIDGET + original.id())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("message").value(containsString("width"))
                .jsonPath("message").value(containsString("null"));

    }

    @Test
    void patchUpdates() {
        var original = model.create(widgetDto1);
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

        var found = model.findById(original.id());
        assertTrue(found.isPresent());
        assertEquals(11, found.get().width());
        assertEquals(original.height(), found.get().height());
        assertEquals(original.z(), found.get().z());
    }
}