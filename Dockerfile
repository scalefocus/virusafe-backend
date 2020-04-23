FROM adoptopenjdk/openjdk11:jre-11.0.6_10-alpine

EXPOSE 8080

ADD target/viru-safe-*.jar viru-safe.jar

ENTRYPOINT ["java", "-jar", "viru-safe.jar"]