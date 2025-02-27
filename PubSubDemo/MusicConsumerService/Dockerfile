FROM maven:3.8.5-openjdk-17 AS build

WORKDIR /app
COPY . .
RUN mvn clean package -pl MusicConsumerService -am

FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/MusicConsumerService/target/*.jar app.jar

ENTRYPOINT ["java","-jar","/app/app.jar"]
