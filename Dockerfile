FROM maven:3.8.4-openjdk-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package

FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY --from=builder /app/target/parser-0.0.1-SNAPSHOT.jar application.jar
CMD ["java", "-jar", "application.jar"]