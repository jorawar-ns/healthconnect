# ── Stage 1: Build ────────────────────────────────────────────────────────
FROM maven:3.8.4-openjdk-8 AS builder
WORKDIR /build

# Copy parent pom + common library first (layer-cache friendly)
COPY pom.xml .
COPY healthconnect-common/pom.xml healthconnect-common/
COPY healthconnect-common/src healthconnect-common/src/
COPY claims-service/pom.xml claims-service/
COPY claims-service/src claims-service/src/

RUN mvn -pl healthconnect-common,claims-service -am clean package -DskipTests -q

# ── Stage 2: Runtime ───────────────────────────────────────────────────────
FROM openjdk:8-jre-slim
WORKDIR /app

# Non-root user for security
RUN addgroup -S hcgroup && adduser -S hcuser -G hcgroup
USER hcuser

COPY --from=builder /build/claims-service/target/*.jar app.jar

EXPOSE 8084

ENTRYPOINT ["java", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-jar", "app.jar"]

