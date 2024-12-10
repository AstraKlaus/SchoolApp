# Используем официальный образ OpenJDK с JDK 18
FROM eclipse-temurin:17-jdk-alpine

# Устанавливаем рабочую директорию внутри контейнера
WORKDIR /app

# Копируем файл с билдом приложения (JAR-файл) в контейнер
COPY build/libs/School-0.0.1-SNAPSHOT.jar school-app.jar

EXPOSE 8084

# Команда для запуска приложения
ENTRYPOINT ["java", "-jar", "school-app.jar"]