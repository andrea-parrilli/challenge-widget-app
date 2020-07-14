package ma.ap.challenge.widgetapp.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WidgetAppServer {
    @SuppressWarnings("squid:S4823") // the arguments are sanitized by spring boot
    public static void main(String[] args) {
        SpringApplication.run(WidgetAppServer.class, args);
    }
}