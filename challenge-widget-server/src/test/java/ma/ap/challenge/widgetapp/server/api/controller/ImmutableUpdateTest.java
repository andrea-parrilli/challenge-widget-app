package ma.ap.challenge.widgetapp.server.api.controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.ap.challenge.widgetapp.core.model.Widget;
import org.junit.jupiter.api.Test;

public class ImmutableUpdateTest {

    @Test
    void deserializeUpdate() throws JsonProcessingException {
        Widget widget = new Widget(1L,2, 3, 4);
        String updateString = """
                {"width":22}
                """;

        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        var example = mapper.writeValueAsString(widget);

        Widget.WidgetBuilder b = widget.toBuilder();

        var exampleBuilder = mapper.writeValueAsString(b);

        var result = (Widget.WidgetBuilder)mapper.readerForUpdating(b).readValue(updateString);

        var updated = result.build();

    }
}
