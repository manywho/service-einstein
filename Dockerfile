FROM maven:onbuild-alpine

EXPOSE 8080

CMD ["java", "-Xmx500m", "-jar", "/usr/src/app/target/einstein-1.0-SNAPSHOT.jar"]
