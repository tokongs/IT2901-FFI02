index 35dcb53..91178f8 100644
--- a/broker/docker/Dockerfile
+++ b/broker/docker/Dockerfile
@@ -1,22 +1,15 @@
 # We use multi-stage here to unzip in an initial layer so we don't have to COPY and then RUN unzip (two layers). ADD can lead to larger layers as well.
-FROM gradle:6.3-jdk11 AS build
+FROM busybox AS unzip
 
-WORKDIR /home/gradle/broker
-RUN git clone https://github.com/hivemq/hivemq-extension-sdk.git ../hivemq-extension-sdk \
-    && cd ../hivemq-extension-sdk \
-    && git reset --hard 56fd3489107934ac073c53adbc6a1709ec5c34ce
+ARG HIVEMQ_VERSION=2019.1
 
+COPY hivemq-ce-${HIVEMQ_VERSION}.zip /
 
-COPY build.gradle .
-COPY gradle.properties .
-COPY settings.gradle .
-COPY src src
-
-RUN gradle packaging
+RUN unzip /hivemq-ce-${HIVEMQ_VERSION}.zip
 
 FROM openjdk:11-jre-slim
 
-ARG HIVEMQ_VERSION=2021.1
+ARG HIVEMQ_VERSION=2019.1
 
 ENV HIVEMQ_GID=10000
 ENV HIVEMQ_UID=10000
@@ -47,11 +40,11 @@ RUN set -x \
         && gosu nobody true \
         && apt-get purge -y gpg dirmngr && rm -rf /var/lib/apt/lists/*
 
-COPY ./docker/config.xml /opt/config.xml
-COPY ./docker/docker-entrypoint.sh /opt/docker-entrypoint.sh
+COPY config.xml /opt/config.xml
+COPY docker-entrypoint.sh /opt/docker-entrypoint.sh
 
 # HiveMQ setup
-COPY --from=build /home/gradle/broker/build/package/hivemq-ce-${HIVEMQ_VERSION} /opt/hivemq-ce-${HIVEMQ_VERSION}
+COPY --from=unzip /hivemq-ce-${HIVEMQ_VERSION} /opt/hivemq-ce-${HIVEMQ_VERSION}
 RUN ln -s /opt/hivemq-ce-${HIVEMQ_VERSION} /opt/hivemq \
     && mv /opt/config.xml /opt/hivemq/conf/config.xml \
     && groupadd --gid ${HIVEMQ_GID} hivemq \
