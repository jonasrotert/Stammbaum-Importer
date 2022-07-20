FROM alpine:3.14
RUN apk --no-cache add openjdk11 --repository=http://dl-cdn.alpinelinux.org/alpine/edge/community
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]