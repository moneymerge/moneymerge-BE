FROM openjdk:17
COPY build/libs/moneymerge-BE-0.0.1-SNAPSHOT.jar app.jar
COPY src/main/resources/static /static
ENTRYPOINT ["java", "-jar", "app.jar"]
