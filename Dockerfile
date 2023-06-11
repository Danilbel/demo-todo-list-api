FROM openjdk:17-jdk-alpine3.14
COPY build/libs/demo-todo-list-api-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
