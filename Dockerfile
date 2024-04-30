# 17 Версия Java
FROM openjdk:17.0.2-jdk-slim-buster

# Переменная JAR_FILE, которая ссылается на скомпилированный .jar файл проекта
ARG JAR_FILE=target/*.jar

# Копирование переменных окружений
COPY ${JAR_FILE} app.jar

# Выполнение команды для запуска .jar - файла
ENTRYPOINT ["java", "-jar", "/app.jar"]

LABEL maintainer="spring-image"

