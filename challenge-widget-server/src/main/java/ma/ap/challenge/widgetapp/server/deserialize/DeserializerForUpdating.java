package ma.ap.challenge.widgetapp.server.deserialize;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import me.ap.challenge.widgetapp.core.deserialize.Buildable;
import me.ap.challenge.widgetapp.core.deserialize.ToBuilderable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

/**
 * Utility service to perform an update of a {@link ToBuilderable} {@code record} using Jackson's most efficient tools.
 */
@Service
@AllArgsConstructor
public class DeserializerForUpdating {
    private final ObjectMapper mapper;

    /**
     * Update a {@link ToBuilderable} instance with the given JSON String.
     * <br/>
     * For example assume {@code r} to be and instance of {@link ToBuilderable} with a property called {code x}.
     * Then calling this method with {@code {"x":33}} has the effect of converting {@code r} to its builder, setting
     * {@code x} on the builder and then to return a newly built instance of {@code R}.
     *
     * @param record the instance to update
     * @param json the JSON containing the properties to update.
     * @return a new instance of {@code R} with the same properties of {@code record}, modified by the given JSON.
     * @param <R> the type of the DTO tu update
     * @param <B> the type of the builder that builds {@code R}
     * @throws JsonProcessingException when the given JSON is invalid, depending on the {@link ObjectMapper} configuration.
     */
    public <R extends ToBuilderable<B>, B extends Buildable<R>> R updateFromJson(R record, String json) throws JsonProcessingException {
        B builder = record.toBuilder();

        mapper.readerForUpdating(builder).readValue(json);

        return builder.build();
    }

    /**
     * Update a {@link ToBuilderable} instance with the given JSON String as an {@link InputStream}.
     * <br/>
     * For example assume {@code r} to be and instance of {@link ToBuilderable} with a property called {code x}.
     * Then calling this method with a stream containing {@code {"x":33}} has the effect of converting {@code r} to its builder, setting
     * {@code x = 33} on the builder and then to return a newly built instance of {@code R}.
     *
     * @param record the instance to update
     * @param jsonStream a JSON stream containing the properties to update.
     * @return a new instance of {@code R} with the same properties of {@code record}, modified by the given JSON.
     * @param <R> the type of the DTO tu update
     * @param <B> the type of the builder that builds {@code R}
     * @throws JsonProcessingException when the given JSON is invalid, depending on the {@link ObjectMapper} configuration.
     * @throws IOException when the stream is not readable.
     */
    public <R extends ToBuilderable<B>, B extends Buildable<R>> R updateFromJson(R record, InputStream jsonStream) throws IOException {
        B builder = record.toBuilder();

        mapper.readerForUpdating(builder).readValue(jsonStream);

        return builder.build();
    }
}
