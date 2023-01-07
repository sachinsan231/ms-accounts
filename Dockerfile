FROM openjdk:11-jdk-slim

#maintainer sachin
MAINTAINER SACHIN
VOLUME /tmp
ARG JAR_FILE=target/*.jar

#add applcation jar to container
COPY ${JAR_FILE} app.jar

#execute application
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]
