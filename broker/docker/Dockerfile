# We use multi-stage here to unzip in an initial layer so we don't have to COPY and then RUN unzip (two layers). ADD can lead to larger layers as well.
FROM gradle:6.3-jdk11 AS build

WORKDIR /home/gradle/broker
RUN git clone https://github.com/hivemq/hivemq-extension-sdk.git ../hivemq-extension-sdk \
    && cd ../hivemq-extension-sdk \
    && git reset --hard 56fd3489107934ac073c53adbc6a1709ec5c34ce


COPY build.gradle .
COPY gradle.properties .
COPY settings.gradle .
COPY src src

RUN gradle packaging

FROM openjdk:11-jre-slim

ARG HIVEMQ_VERSION=2021.1

ENV HIVEMQ_GID=10000
ENV HIVEMQ_UID=10000

# Additional JVM options, may be overwritten by user
ENV JAVA_OPTS "-XX:+UnlockExperimentalVMOptions -XX:+UseNUMA"

# Default allow all extension, set this to false to disable it
ENV HIVEMQ_ALLOW_ALL_CLIENTS "true"

# gosu for root step-down to user-privileged process
ENV GOSU_VERSION 1.11

# Set locale
ENV LANG=en_US.UTF-8

# gosu setup
RUN set -x \
        && apt-get update && apt-get install -y --no-install-recommends curl gnupg-agent gnupg dirmngr \
        && curl -fSL "https://github.com/tianon/gosu/releases/download/$GOSU_VERSION/gosu-$(dpkg --print-architecture)" -o /usr/local/bin/gosu \
        && curl -fSL "https://github.com/tianon/gosu/releases/download/$GOSU_VERSION/gosu-$(dpkg --print-architecture).asc" -o /usr/local/bin/gosu.asc \
        && export GNUPGHOME="$(mktemp -d)" \
        && gpg --batch --keyserver ha.pool.sks-keyservers.net --recv-keys B42F6819007F00F88E364FD4036A9C25BF357DD4 \
        && gpg --batch --verify /usr/local/bin/gosu.asc /usr/local/bin/gosu \
        && rm -rf "$GNUPGHOME" /usr/local/bin/gosu.asc \
        && { command -v gpgconf && gpgconf --kill all || :; } \
        && chmod +x /usr/local/bin/gosu \
        && gosu nobody true \
        && apt-get purge -y gpg dirmngr && rm -rf /var/lib/apt/lists/*

COPY ./docker/config.xml /opt/config.xml
COPY ./docker/docker-entrypoint.sh /opt/docker-entrypoint.sh

# HiveMQ setup
COPY --from=build /home/gradle/broker/build/package/hivemq-ce-${HIVEMQ_VERSION} /opt/hivemq-ce-${HIVEMQ_VERSION}
RUN ln -s /opt/hivemq-ce-${HIVEMQ_VERSION} /opt/hivemq \
    && mv /opt/config.xml /opt/hivemq/conf/config.xml \
    && groupadd --gid ${HIVEMQ_GID} hivemq \
    && useradd -g hivemq -d /opt/hivemq -s /bin/bash --uid ${HIVEMQ_UID} hivemq \
    && chmod -R 775 /opt \
    && chmod +x /opt/hivemq/bin/run.sh /opt/docker-entrypoint.sh

# Make broker data persistent throughout stop/start cycles
VOLUME /opt/hivemq/data

# Persist log data
VOLUME /opt/hivemq/log

#mqtt-clients
EXPOSE 1883

#websockets
EXPOSE 8000

WORKDIR /opt/hivemq

ENTRYPOINT ["/opt/docker-entrypoint.sh"]
CMD ["/opt/hivemq/bin/run.sh"]
