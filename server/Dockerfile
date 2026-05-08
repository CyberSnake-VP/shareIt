FROM eclipse-temurin:22-jre-jammy
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
# команда sh запускает shell , а -с указывает что запусти следующую строку как команду
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar"]