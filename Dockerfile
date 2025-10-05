FROM maven:3.9.6-eclipse-temurin-11 AS build
WORKDIR /app
COPY . .
RUN mvn clean install -DskipTests

FROM eclipse-temurin:11-jre
WORKDIR /app
COPY --from=build /app .
CMD ["mvn", "clean", "test"]

