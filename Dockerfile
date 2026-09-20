# Multi-stage build: compile the .war with Maven, run it on Tomcat 9 — the
# "PaaS with Java/Tomcat or Docker support" platform option from README §6 / spec §10.

FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
COPY schema.sql seed.sql ./
# Cache dependencies in their own layer before copying source.
RUN mvn -B -q dependency:go-offline || true
COPY src ./src
COPY checkstyle.xml .
RUN mvn -B -q clean package -DskipTests

FROM tomcat:9.0-jdk17-temurin
# curl is used by docker-compose.yml's healthcheck; not present in the base image.
RUN apt-get update && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*
# Remove Tomcat's default sample webapps so only AdharshMart is served.
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /build/target/adharshmart.war /usr/local/tomcat/webapps/adharshmart.war
EXPOSE 8080
