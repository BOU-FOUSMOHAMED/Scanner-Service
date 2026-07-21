
FROM maven:3.9.11-eclipse-temurin-25 AS builder

WORKDIR /app

COPY . .


RUN  mvnw clean package -DskipTests

FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]

# FROM eclipse-temurin:25-jre
# #pas toucher sa


# COPY  target/*.jar app.jar
# ENTRYPOINT ["java", "-jar", "app.jar"]