# ── Stage 1: Build ────────────────────────────────────────────────────────
FROM maven:3.8.4-openjdk-8 AS builder
WORKDIR /build

# Copy parent pom + common library first (layer-cache friendly)
COPY pom.xml .
COPY healthconnect-common/pom.xml healthconnect-common/
COPY healthconnect-common/src healthconnect-common/src/
COPY auth-service/pom.xml auth-service/
COPY auth-service/src auth-service/src/

RUN mvn -pl healthconnect-common,auth-service -am clean package -DskipTests -q

# ── Stage 2: Runtime ───────────────────────────────────────────────────────
FROM openjdk:8-jre-slim
WORKDIR /app

# Non-root user for security
RUN addgroup -S hcgroup && adduser -S hcuser -G hcgroup
USER hcuser

COPY --from=builder /build/auth-service/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-jar", "app.jar"]

