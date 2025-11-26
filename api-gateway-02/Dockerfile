FROM eclipse-temurin:17-jdk
COPY target/api-gateway-0.0.1-SNAPSHOT.jar /app/api-gateway.jar
WORKDIR /app
EXPOSE 8030
ENTRYPOINT ["java", "-jar", "api-gateway.jar"]