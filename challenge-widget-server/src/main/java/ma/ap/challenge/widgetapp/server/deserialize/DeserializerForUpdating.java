package ma.ap.challenge.widgetapp.server.deserialize;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DeserializerForUpdating {
    private final ObjectMapper mapper;

    public <T> T updateRecordFromJson<T>
}
