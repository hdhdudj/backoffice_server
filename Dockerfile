FROM gradle:jdk-alpine

WORKDIR /home/ec2-user/trdst_back_office

EXPOSE 8080

USER root

RUN apk update

ENV GRADLE_USER_HOME /home/ec2-user/trdst_back_office

COPY . /home/ec2-user/trdst_back_office

RUN gradle build


FROM java:jre-alpine

WORKDIR /home/ec2-user/trdst_back_office

COPY --from=0 /home/ec2-user/trdst_back_office/backoffice_server-0.0.1-SNAPSHOT.jar .

#ENTRYPOINT java -jar backoffice_server-0.0.1-SNAPSHOT.jar
ENTRYPOINT java -Dlog4j2.formatMsgNoLookups=true -jar ./backoffice_server-0.0.1-SNAPSHOT.jar --spring.config.location=./config/application.properties,./config/kakaobizmessage.yml
