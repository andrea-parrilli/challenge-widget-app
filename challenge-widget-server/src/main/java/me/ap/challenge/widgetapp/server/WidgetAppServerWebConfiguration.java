package me.ap.challenge.widgetapp.server;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class WidgetAppServerWebConfiguration {
    /**
     * Configure Jackson by applying the Spring defaults, and overriding the strictly necessary configuration.
     * <br/>
     * In particular, to allow for the usage of records as DTO, Jackson must be allowed to reflect and operate on fields.
     *
     * @return the Jackson configuration bean
     */
    @Bean
    public Jackson2ObjectMapperBuilder customizeObjectMapper() {
        return new Jackson2ObjectMapperBuilder()
                // allow reflective access to fields (for records)
                .visibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                // cleanup output: do not emit null fields
                .serializationInclusion(JsonInclude.Include.NON_NULL);
    }
}