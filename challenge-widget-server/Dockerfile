FROM alpine/java:21-jdk
RUN mkdir /widgetapp
WORKDIR /widgetapp
COPY target/challenge-widget-server-*.jar /widgetapp/widgetapp.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "widgetapp.jar"]
