FROM eclipse-temurin:17-jre-alpine

# Install curl
RUN apk add --no-cache curl

RUN addgroup -S spring && adduser -S spring -G spring
USER spring

WORKDIR /app

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENV SPRING_PROFILES_ACTIVE=default

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]