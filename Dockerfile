FROM openjdk:17

ADD target/invoice-system-compose.jar invoice-system-compose.jar

# Copy the packaged JAR file into the container
COPY target/invoice-system-compose.jar invoice-system-compose.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","invoice-system-compose.jar"]