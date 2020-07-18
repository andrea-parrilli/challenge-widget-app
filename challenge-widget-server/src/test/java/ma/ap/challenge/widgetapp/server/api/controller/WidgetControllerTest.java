package ma.ap.challenge.widgetapp.server.api.controller;

import ma.ap.challenge.widgetapp.server.ApiPaths;
import ma.ap.challenge.widgetapp.server.WidgetAppServer;
import me.ap.challenge.widgetapp.core.model.Widget;
import me.ap.challenge.widgetapp.core.service.WidgetService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

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
    }

    @Test
    void getWidget() {
        var widget = widgetService.create(Widget.builder().width(1).height(2).z(3).build());

        api.get().uri(ApiPaths.PATH_WIDGET + widget.getId())
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
    }

    @Test
    void create() {
    }

    @Test
    void delete() {
    }

    @Test
    void update() {
    }
}