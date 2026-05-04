FROM amazoncorretto:21-alpine

#Создаем рабочую папку внутри контейнера
WORKDIR /app

# Копируем наш JAR файл из папки target
COPY target/*.jar app.jar

# Говорим, что приложение работает на порту 8080
EXPOSE 8080

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]
