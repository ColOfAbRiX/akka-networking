FROM openjdk:8-jre-alpine

ADD target/scala-**/consumer.jar consumer.jar

ENTRYPOINT ["java", "-jar", "/consumer.jar"]
