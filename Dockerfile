FROM openjdk:17-jdk-alpine
ARG APP_JAR=target/number-discovery-bot-0.0.1-SNAPSHOT.jar
ENV APP_JAR=$APP_JAR
COPY $APP_JAR /app.jar

CMD ["java", "-jar", "/app.jar"]