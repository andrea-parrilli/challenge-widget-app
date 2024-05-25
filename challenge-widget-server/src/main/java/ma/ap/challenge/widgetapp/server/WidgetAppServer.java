package ma.ap.challenge.widgetapp.server;

import me.ap.challenge.widgetapp.core.WidgetAppCoreConfiguration;
import me.ap.tools.jackson.deserialize.DeserializerForUpdating;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Bootstrap class for Spring.
 * <p>
 * Does not include actual configuration, which is delegated to {@code <...>Configuration} classes, to allow for
 * targeted integration tests, only booting the relevant parts of the context.
 */
@SpringBootApplication(scanBasePackageClasses = {WidgetAppServerWebConfiguration.class,
        WidgetAppCoreConfiguration.class,
        DeserializerForUpdating.class})
public class WidgetAppServer {
    @SuppressWarnings("squid:S4823") // the arguments are sanitized by spring boot
    public static void main(String[] args) {
        SpringApplication.run(WidgetAppServer.class, args);
    }
}
