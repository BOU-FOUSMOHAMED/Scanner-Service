FROM eclipse-temurin:25-jre
#pas toucher sa
RUN ./mvnw clean package -DskipTests


COPY  target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]