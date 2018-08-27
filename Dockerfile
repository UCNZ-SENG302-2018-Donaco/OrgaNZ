FROM openjdk:8-jre
ADD ./target /target
ENV TZ Pacific/Auckland
WORKDIR /target
CMD ["java", "-jar", "organz-server-0.7.jar"]
