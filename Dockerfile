FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace/app

RUN adduser -D rinha && addgroup rinha rinha
RUN chown -R rinha /workspace/app
USER rinha

COPY gradle gradle
COPY build.gradle settings.gradle gradlew ./
COPY src src

RUN ./gradlew clean build -x test
RUN mkdir -p build/libs/dependency && (cd build/libs/dependency; jar -xf ../rinha-de-backend-0.0.1-SNAPSHOT.jar)

FROM eclipse-temurin:21-jdk-alpine
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/build/libs/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
EXPOSE 8080
ENTRYPOINT ["java","-cp","app:app/lib/*","br.com.jeiferson.rinhadebackend.RinhaDeBackendApplication"]