FROM openjdk:11-slim as build

#maintainer sachin
MAINTAINER SACHIN

#add applcation jar to container
COPY target/accounts-0.0.1-SNAPSHOT.jar accounts-0.0.1-SNAPSHOT.jar

#execute application
ENTRYPOINT ["java", "-jar", "accounts-0.0.1-SNAPSHOT.jar"]
