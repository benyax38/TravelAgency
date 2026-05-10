FROM eclipse-temurin:21-jdk-alpine
ARG JAR_FILE=target/travel-agency.jar
COPY ${JAR_FILE} travel-agency.jar
ENTRYPOINT ["java","-jar","/travel-agency.jar"]