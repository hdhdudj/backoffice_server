FROM gradle:jdk-alpine

WORKDIR /backoffice_server

EXPOSE 8080

USER root

RUN apk update

ENV GRADLE_USER_HOME /backoffice_server

COPY . /backoffice_server

RUN gradle build


FROM openjdk:8-jdk-alpine
ARG JAR_FILE=/backoffice_server/build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-Dexternal.app.properties=file:/config/kakaobizmessage.yml","-Dlog4j2.formatMsgNoLookups=true","-jar","/app.jar","--spring.config.location=/config/application.properties,/config/kakaobizmessage.yml"]

#FROM java:jre-alpine



#WORKDIR /backoffice_server
#ARG JAR_FILE=target/*.jar
#COPY ${JAR_FILE} app.jar
#COPY --from=0 /backoffice_server/build/libs/backoffice_server-0.0.1-SNAPSHOT.jar .
#ENTRYPOINT ["java","-Dlog4j2.formatMsgNoLookups=true","-jar","/app.jar","--spring.config.location=/config/application.properties,/config/kakaobizmessage.yml"]
#ENTRYPOINT java -jar backoffice_server-0.0.1-SNAPSHOT.jar
#ENTRYPOINT java -Dlog4j2.formatMsgNoLookups=true -jar ./backoffice_server-0.0.1-SNAPSHOT.jar --spring.config.location=./config/application.properties,./config/kakaobizmessage.yml
#ENTRYPOINT java -Dlog4j2.formatMsgNoLookups=true -jar ./backoffice_server-0.0.1-SNAPSHOT.jar --spring.config.location=/config/application.properties,/config/kakaobizmessage.yml
#ENTRYPOINT ["java","-Dlog4j2.formatMsgNoLookups=true","-jar","./backoffice_server-0.0.1-SNAPSHOT.jar","--spring.config.location=/config/application.properties,/config/kakaobizmessage.yml"]

##