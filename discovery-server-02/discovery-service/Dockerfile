FROM eclipse-temurin:17-jdk
COPY target/discovery-service-0.0.1-SNAPSHOT.jar /app/discovery-service.jar
WORKDIR /app
EXPOSE 8010
ENTRYPOINT ["java", "-jar", "discovery-service.jar"]