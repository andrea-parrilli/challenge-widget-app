package me.ap.challenge.widgetapp.server;

import me.ap.challenge.widgetapp.core.WidgetAppCoreConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackageClasses = WidgetAppCoreConfiguration.class)
@EntityScan(basePackageClasses = WidgetAppCoreConfiguration.class)
public class WidgetAppServerPersistenceConfiguration {
}
