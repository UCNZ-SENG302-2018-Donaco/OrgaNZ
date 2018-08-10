FROM openjdk:8-jre
ADD ./target /target
RUN apk add --no-cache tzdata
ENV TZ Pacific/Auckland
WORKDIR /target
CMD ["java", "-jar", "organz-server-0.5.jar"]
