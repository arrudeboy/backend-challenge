FROM eclipse-temurin:21-jdk-alpine as builder

WORKDIR /app

COPY mvnw pom.xml ./
COPY .mvn .mvn

COPY src src

RUN ./mvnw clean package

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
