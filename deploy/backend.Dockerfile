FROM registry.steam.crcloud.com/tools/eclipse-temurin:17-jdk

ARG APP_VERSION=0.1.0
ENV APP_HOME=/opt/finance-agent
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0"

USER root
RUN groupadd --system --gid 10001 finance-agent \
    && useradd --system --uid 10001 --gid 10001 --home-dir ${APP_HOME} --shell /usr/sbin/nologin finance-agent \
    && mkdir -p ${APP_HOME}/.runtime \
    && chown -R finance-agent:finance-agent ${APP_HOME}

WORKDIR ${APP_HOME}
COPY --chown=finance-agent:finance-agent deploy/artifacts/app.jar ${APP_HOME}/app.jar

LABEL org.opencontainers.image.title="finance-agent-backend" \
      org.opencontainers.image.version="${APP_VERSION}"

USER 10001:10001
# 端口由 compose 的 FINANCE_AGENT_PORT 控制，默认 8080。
HEALTHCHECK --interval=30s --timeout=5s --start-period=20s --retries=3 \
    CMD bash -c 'exec 3<>/dev/tcp/127.0.0.1/${FINANCE_AGENT_PORT:-8080}' || exit 1

ENTRYPOINT ["java", "-jar", "/opt/finance-agent/app.jar"]
