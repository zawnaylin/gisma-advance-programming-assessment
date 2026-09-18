# syntax=docker/dockerfile:1

# ---- Build stage: compile the app and the Vaadin production frontend bundle ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml ./
RUN --mount=type=cache,target=/root/.m2 mvn -B -Pproduction dependency:go-offline -DskipTests || true

COPY package.json package-lock.json tsconfig.json types.d.ts vite.config.ts ./
COPY src ./src
# The Vaadin plugin downloads Node.js into ~/.vaadin, so cache that alongside ~/.m2.
RUN --mount=type=cache,target=/root/.m2 \
    --mount=type=cache,target=/root/.vaadin \
    mvn -B -Pproduction -DskipTests package \
    && cp target/app-*.jar /workspace/app.jar

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S app && adduser -S app -G app
COPY --from=build /workspace/app.jar app.jar
USER app

EXPOSE 8080
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75"
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
