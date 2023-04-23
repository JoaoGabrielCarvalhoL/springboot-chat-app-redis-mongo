FROM openjdk:17-alpine
MAINTAINER 27.joaogabriel@gmail.com
EXPOSE 80
RUN mkdir /app
COPY build/libs/chat-0.0.1-SNAPSHOT.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]