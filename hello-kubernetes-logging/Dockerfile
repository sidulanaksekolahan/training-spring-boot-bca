FROM eclipse-temurin:17-jdk
COPY target/hello-kubernetes-0.0.1-SNAPSHOT.jar /app/hello-app.jar
WORKDIR /app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "hello-app.jar"]