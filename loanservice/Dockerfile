#Start with a base image containing Java runtime
FROM openjdk:11-slim as build

#Information around who maintain the image
MAINTAINER test14.com

# Add the application's jar to the container
COPY target/loanservice-0.0.1-SNAPSHOT.jar  loanservice-0.0.1-SNAPSHOT.jar

#execute the application
ENTRYPOINT ["java","-jar","/loanservice-0.0.1-SNAPSHOT.jar"]
