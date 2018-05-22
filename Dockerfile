FROM openjdk

RUN mkdir /app

COPY gradle /app/gradle
COPY src /app/src
COPY build.gradle /app
COPY settings.gradle /app
COPY gradlew /app

ENV SPRING_PROFILES_ACTIVE ${SPRING_PROFILES_ACTIVE}

RUN cd /app \
    && chmod +x ./gradlew \
    && ./gradlew clean \
    && ./gradlew build -x test \
    && cp $(find . -name listener*.jar) /app.jar \
    && sh -c 'chmod +x /app.jar' \
    && cd / \
    && rm -rf /app

EXPOSE 8080

CMD [ "java", "-Xss15m", "-Xms256m", "-Xmx500m", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar" ]
