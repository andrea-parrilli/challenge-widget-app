package ma.ap.challenge.widgetapp.server;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import me.ap.challenge.widgetapp.core.WidgetAppCoreConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@SpringBootApplication(scanBasePackageClasses = {WidgetAppServer.class, WidgetAppCoreConfiguration.class})
public class WidgetAppServer {
    @SuppressWarnings("squid:S4823") // the arguments are sanitized by spring boot
    public static void main(String[] args) {
        SpringApplication.run(WidgetAppServer.class, args);
    }

    /**
     * Configure Jackson by applying the Spring defaults, and overriding the strictly necessary configuration.
     *
     * @return the configuration bean
     */
    @Bean
    public Jackson2ObjectMapperBuilder cutomizeObjectMapper() {
        return new Jackson2ObjectMapperBuilder().visibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }
}