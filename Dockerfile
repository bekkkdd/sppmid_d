FROM maven:3.5.3-jdk-8-alpine AS MAVEN_BUILD
MAINTAINER Bitlab
COPY pom.xml /build/
COPY src /build/src/

WORKDIR /build/
RUN mvn dependency:go-offline

RUN mvn package
FROM openjdk:8-jre-alpine
WORKDIR /app
COPY --from=MAVEN_BUILD /build/target/*.jar /app/*.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "*.jar"]
