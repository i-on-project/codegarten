FROM adoptopenjdk/openjdk11:alpine

ARG DEPENDENCY=build/extracted
WORKDIR /app

COPY ${DEPENDENCY}/BOOT-INF/lib ./server/lib
COPY ${DEPENDENCY}/META-INF ./server/META-INF
COPY secrets ./secrets
COPY ${DEPENDENCY}/BOOT-INF/classes ./server

EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-Xmx256m", "-cp", "server:server/lib/*", "org.ionproject.codegarten.CodeGartenApplicationKt"]