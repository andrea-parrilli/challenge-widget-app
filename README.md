# Widget Application Challenge

A solution to the Widget application challenge (Miro? draw.io? can't remember).
Or at least it was.
Now it is a playground to experiment with, and showcase the use of, current web app technology and the latest Java
features.

# Build

To build the project, first build and install [tools-all](https://github.com/andrea-parrilli/tools-all), which is a dependency that is not published yet, by issuing, at the root of `tools/all` the maven command `mvn install`.

# OpenAPI and Swagger

API documentation is available as an open API spec at:

    /v3/api-docs

The Swagger UI at:

    /swagger-ui/index.html

# Documentation

The project documentation can be found at:

    /docs/index.html

# Docker

To build a docker image, run `challenge-widget-server/dockerbuild.sh`.
This will build the application image and tag it with `ap/widgetapp` and the version of the server module, as per
its `pom`.

To run the image, assuming port 8080 is unbound on the host machine, execute

    docker run  -p 8080:8080 ap/widgetapp:<your version>

# Coverage

The project collects coverage information and makes an aggregated JaCoCo report in module `coverage-reports`.
Single modules do not produce coverage reports to keep the build a bit leaner.
Use IntelliJ `run with coverage` option instead.
Due to the integration with Maven lifecycle, the report can only be built during full builds of the parent module.
Otherwise it will be empty.
