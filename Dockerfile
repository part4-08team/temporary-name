# 빌드 스테이지
FROM amazoncorretto:17 AS builder

WORKDIR /app

COPY gradle ./gradle
COPY gradlew ./gradlew
COPY build.gradle settings.gradle ./
RUN chmod +x gradlew

RUN ./gradlew build -x test --no-daemon || true

COPY src ./src
RUN ./gradlew build -x test --no-daemon

# 런타임 스테이지
FROM amazoncorretto:17-alpine3.21

WORKDIR /app
RUN apk add --no-cache tzdata

ENV JVM_OPTS="-Duser.timezone=Asia/Seoul"

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 80

ENTRYPOINT ["sh", "-c", "java ${JVM_OPTS} -jar app.jar"]
