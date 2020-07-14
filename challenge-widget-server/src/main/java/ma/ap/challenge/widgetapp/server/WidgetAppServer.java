package ma.ap.challenge.widgetapp.server;

import me.ap.challenge.widgetapp.core.WidgetAppCoreConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackageClasses = {WidgetAppServer.class, WidgetAppCoreConfiguration.class})
public class WidgetAppServer {
    @SuppressWarnings("squid:S4823") // the arguments are sanitized by spring boot
    public static void main(String[] args) {
        SpringApplication.run(WidgetAppServer.class, args);
    }
}