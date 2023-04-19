FROM openjdk:11-jdk-slim

WORKDIR /app

COPY build/libs/meca-0.0.1-SNAPSHOT.jar app.jar

ENV SERVER_MODE default
ENV PORT 8000
ENV LOCATION "/"

ENTRYPOINT ["java","-Dserver.port=${PORT}","-Dspring.profiles.active=${SERVER_MODE}", "-jar", "app.jar", "--spring.config.location=${LOCATION}", "-Duser.timezone=${TZ}"]