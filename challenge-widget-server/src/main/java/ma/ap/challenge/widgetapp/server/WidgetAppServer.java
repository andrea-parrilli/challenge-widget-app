package ma.ap.challenge.widgetapp.server;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import me.ap.challenge.widgetapp.core.WidgetAppCoreConfiguration;
import me.ap.tools.jackson.deserialize.DeserializerForUpdating;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@SpringBootApplication(scanBasePackageClasses = {WidgetAppServer.class,
        WidgetAppCoreConfiguration.class,
        DeserializerForUpdating.class})
@EnableJpaRepositories(basePackageClasses = WidgetAppCoreConfiguration.class)
@EntityScan(basePackageClasses = WidgetAppCoreConfiguration.class)
public class WidgetAppServer {
    @SuppressWarnings("squid:S4823") // the arguments are sanitized by spring boot
    public static void main(String[] args) {
        SpringApplication.run(WidgetAppServer.class, args);
    }

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