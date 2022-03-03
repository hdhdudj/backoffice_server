FROM gradle:jdk-alpine AS builder 

WORKDIR /backoffice_server

USER root

RUN apk update

ENV GRADLE_USER_HOME /backoffice_server

COPY . /backoffice_server

RUN gradle build

FROM openjdk:8-jdk-alpine
#ARG JAR_FILE=/backoffice_server/*.jar
COPY --from=builder /backoffice_server/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-Dlog4j2.formatMsgNoLookups=true","-jar","/app.jar","--spring.config.location=/config/application.properties,/config/kakaobizmessage.yml"]

#FROM java:jre-alpine

#WORKDIR /backoffice_server
#ARG JAR_FILE=target/*.jar
#COPY ${JAR_FILE} app.ja
