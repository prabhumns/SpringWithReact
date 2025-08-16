FROM amazoncorretto:21-alpine-jdk

RUN apk add --no-cache shadow

RUN groupadd -g 1001 app-group && \
    useradd -u 1001 -g 1001 app-user

USER app-user

WORKDIR /home/app-user

COPY target/*.jar app.jar

ENTRYPOINT ["java","-jar","app.jar", "--spring.profiles.active=prod"]
