package ma.ap.challenge.widgetapp.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.OperationsSorter;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

import static ma.ap.challenge.widgetapp.server.ApiPaths.PATH_WIDGET;
import static springfox.documentation.builders.PathSelectors.ant;
import static springfox.documentation.builders.RequestHandlerSelectors.any;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {
    private final BuildProperties properties;

    SwaggerConfiguration(@Autowired(required = false) BuildProperties properties) {
        this.properties = properties;
    }

    @Bean
    public UiConfiguration swaggerUiConfig() {
        return UiConfigurationBuilder
                .builder()
                .operationsSorter(OperationsSorter.METHOD)
                .build();
    }

    @Bean
    public Docket internalApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("Widget API")
                .select()
                .apis(any())
                .paths(ant(PATH_WIDGET + "**"))
                .build()
                .apiInfo(new ApiInfo("Widget API",
                        "This API does terrible things to Widgets. Beware.",
                        properties == null ? "Development" : properties.getVersion(),
                        null,
                        new Contact("Widget Support", "https://www.amazingwidgets.com/support", "support@amazingwidgets.com"),
                        null,
                        null,
                        Collections.emptyList()))
                .useDefaultResponseMessages(true)
                .tags(new Tag("Widgets", "Manage Widgets"));
    }
}
