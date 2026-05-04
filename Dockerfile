FROM amazoncorretto:21-alpine

#Установка таймзон (для тестов).. т.к. тесты просто без временной зоны приходят
RUN apk add --no-cache tzdata
ENV TZ=Europe/Moscow
# Всё! Alpine сам создаст /etc/localtime и /etc/timezone

#Создаем рабочую папку внутри контейнера
WORKDIR /app

# Копируем наш JAR файл из папки target
COPY target/*.jar app.jar

# Говорим, что приложение работает на порту 8080
EXPOSE 8080

# Запускаем приложение можно прям здесь указать профиль, но лучше в docker-compose
#ENTRYPOINT ["java","-Dspring.profiles.active=docker", "-jar", "app.jar"]

ENTRYPOINT ["java", "-jar", "app.jar"]