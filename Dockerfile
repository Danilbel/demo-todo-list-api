FROM openjdk:17-jdk-alpine3.14

COPY . /app
WORKDIR /app

RUN chmod +x gradlew

RUN ./gradlew build

CMD ["java", "-jar", "./build/libs/demo-todo-list-api-1.0-SNAPSHOT.jar"]
