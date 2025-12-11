FROM eclipse-temurin:21-jre
WORKDIR /app
RUN useradd -m springuser
USER springuser
COPY target/cloudKitchenBackend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 5000
ENTRYPOINT ["java", "-jar", "app.jar"]

