FROM maven:3.9.11-eclipse-temurin-17 AS backend-build

ARG APP_VERSION=0.1.0
WORKDIR /workspace
COPY backend/pom.xml backend/pom.xml
COPY backend/ backend/
RUN mvn -f backend/pom.xml -DskipTests clean package \
    && test -f backend/target/finance-agent-${APP_VERSION}.jar

FROM eclipse-temurin:17-jre-jammy

ARG APP_VERSION=0.1.0
ENV APP_HOME=/opt/finance-agent
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0"

USER root
RUN apt-get update \
    && apt-get install --no-install-recommends --yes curl \
    && rm -rf /var/lib/apt/lists/*

RUN groupadd --system --gid 10001 finance-agent \
    && useradd --system --uid 10001 --gid 10001 --home-dir ${APP_HOME} --shell /usr/sbin/nologin finance-agent \
    && mkdir -p ${APP_HOME} \
    && chown -R finance-agent:finance-agent ${APP_HOME}

WORKDIR ${APP_HOME}
COPY --from=backend-build --chown=finance-agent:finance-agent \
    /workspace/backend/target/finance-agent-${APP_VERSION}.jar ${APP_HOME}/app.jar

LABEL org.opencontainers.image.title="finance-agent-backend" \
      org.opencontainers.image.version="${APP_VERSION}"

USER 10001:10001
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s --start-period=20s --retries=3 \
    CMD curl --fail --silent http://127.0.0.1:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "/opt/finance-agent/app.jar"]
