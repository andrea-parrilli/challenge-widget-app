package ma.ap.challenge.widgetapp.server.deserialize;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import me.ap.challenge.widgetapp.core.deserialize.Buildable;
import me.ap.challenge.widgetapp.core.deserialize.ToBuilderable;
import org.springframework.boot.autoconfigure.info.ProjectInfoProperties;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DeserializerForUpdating {
    private final ObjectMapper mapper;

    public <R extends ToBuilderable<B>, B extends Buildable<R>> R updateRecordFromJson(R record, String json) throws JsonProcessingException {
        B builder = record.toBuilder();

        mapper.readerForUpdating(builder).readValue(json);

        return builder.build();
    }
}
