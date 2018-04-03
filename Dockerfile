FROM openjdk

RUN mkdir /app

COPY gradle /app/gradle
COPY src /app/src
COPY build.gradle /app
COPY gradlew /app

ENV JAR_PREFIX=""

RUN cd /app \
    && ./gradlew clean \
    && ./gradlew build -x test \
    && cp $(find . -name ${JAR_PREFIX}*.jar) /app.jar \
    && sh -c 'chmod +x /app.jar' \
    && cd / \
    && rm -rf /app

EXPOSE 8080

CMD [ "java", "-Xss15m", "-Xms256m", "-Xmx988m", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar" ]