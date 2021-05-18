FROM gcr.io/distroless/java:8
ADD target/processor-webapp-0.0.1-SNAPSHOT.jar processor-webapp-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","processor-webapp-0.0.1-SNAPSHOT.jar"]
EXPOSE 8080