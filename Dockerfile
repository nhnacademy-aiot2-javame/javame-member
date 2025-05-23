FROM ubuntu:latest
LABEL authors="jangwonseong"

ENTRYPOINT ["top", "-b"]

FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/*.jar javame-member.jar

EXPOSE 10276

CMD ["java", "-jar", "javame-member.jar"]