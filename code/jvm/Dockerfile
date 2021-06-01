FROM adoptopenjdk/openjdk11:alpine

ARG DEPENDENCY=build/extracted
WORKDIR /app

COPY ${DEPENDENCY}/BOOT-INF/lib ./server/lib
COPY ${DEPENDENCY}/META-INF ./server/META-INF
COPY secrets ./secrets
COPY ${DEPENDENCY}/BOOT-INF/classes ./server

ENV CODEGARTEN_GITHUB_APP_PROPERTIES_PATH=secrets/github-app-properties.json
ENV CODEGARTEN_GITHUB_APP_PRIVATE_KEY_PATH=secrets/gh-app-private-key.pem
ENV CODEGARTEN_CIPHER_KEY_PATH=secrets/cipher-key.txt

EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-Xmx256m", "-cp", "server:server/lib/*", "org.ionproject.codegarten.CodeGartenApplicationKt"]