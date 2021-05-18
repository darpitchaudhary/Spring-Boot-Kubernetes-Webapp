FROM gcr.io/distroless/java:8
ADD target/notifier-0.0.1-SNAPSHOT.jar notifier-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","notifier-0.0.1-SNAPSHOT.jar"]
EXPOSE 8080
