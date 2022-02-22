FROM gradle:jdk-alpine

WORKDIR /Users/pecan/Dev/code/java/backoffice_server

EXPOSE 8080

USER root

RUN apk update

ENV GRADLE_USER_HOME /Users/pecan/Dev/code/java/backoffice_server

COPY . /Users/pecan/Dev/code/java/backoffice_server

RUN gradle build


FROM java:jre-alpine

WORKDIR /Users/pecan/Dev/code/java/backoffice_server

COPY --from=0 /Users/pecan/Dev/code/java/backoffice_server/build/libs/backoffice_server-0.0.1-SNAPSHOT.jar .

#ENTRYPOINT java -jar backoffice_server-0.0.1-SNAPSHOT.jar
ENTRYPOINT java -Dlog4j2.formatMsgNoLookups=true -jar ./backoffice_server-0.0.1-SNAPSHOT.jar --spring.config.location=./config/application.properties,./config/kakaobizmessage.yml
