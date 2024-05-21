package me.ap.challenge.widgetapp.core;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Spring configuration for tests.
 */
@SpringBootApplication
@EnableJpaRepositories
@EntityScan
public class WidgetAppCoreTestConfiguration {
}