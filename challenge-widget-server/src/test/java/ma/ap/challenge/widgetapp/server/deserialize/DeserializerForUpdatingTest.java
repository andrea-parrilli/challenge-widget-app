package ma.ap.challenge.widgetapp.server.deserialize;

import com.fasterxml.jackson.core.JsonProcessingException;
import ma.ap.challenge.widgetapp.server.WidgetAppServer;
import me.ap.challenge.widgetapp.core.model.Widget;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {WidgetAppServer.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DeserializerForUpdatingTest {
    @Autowired
    private DeserializerForUpdating updater;

    @Test
    void deserializeForUpdate_String() throws JsonProcessingException {
        Widget original = new Widget(1L, 2, 3, 4);
        String json = """
                {"z":44}
                """;

        Widget updated = updater.updateRecordFromJson(original, json);

        assertEquals(44, updated.z());
    }

    @Test
    void deserializeForUpdate_Stream() throws IOException {
        Widget original = new Widget(1L, 2, 3, 4);
        String json = """
                {"z":44}
                """;
        var jsonStream = new ByteArrayInputStream(json.getBytes());
        Widget updated = updater.updateRecordFromJson(original, jsonStream);

        assertEquals(44, updated.z());
    }
}