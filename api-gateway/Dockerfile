# ── Stage 1: Build ────────────────────────────────────────────────────────
FROM maven:3.8.4-openjdk-8 AS builder
WORKDIR /build

# Copy parent pom + common library first (layer-cache friendly)
COPY pom.xml .
COPY healthconnect-common/pom.xml healthconnect-common/
COPY healthconnect-common/src healthconnect-common/src/
COPY api-gateway/pom.xml api-gateway/
COPY api-gateway/src api-gateway/src/

RUN mvn -pl healthconnect-common,api-gateway -am clean package -DskipTests -q

# ── Stage 2: Runtime ───────────────────────────────────────────────────────
FROM openjdk:8-jre-slim
WORKDIR /app

# Non-root user for security
RUN addgroup -S hcgroup && adduser -S hcuser -G hcgroup
USER hcuser

COPY --from=builder /build/api-gateway/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-jar", "app.jar"]

