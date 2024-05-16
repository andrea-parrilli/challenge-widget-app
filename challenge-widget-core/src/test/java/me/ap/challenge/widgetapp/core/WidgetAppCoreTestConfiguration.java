package me.ap.challenge.widgetapp.core;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Marker interface for Spring to scan the core package.
 */
@SpringBootApplication
@EnableJpaRepositories
@EntityScan
public class WidgetAppCoreTestConfiguration {
}