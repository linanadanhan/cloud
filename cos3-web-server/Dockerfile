FROM openjdk:8-jdk-alpine
MAINTAINER shencq <shencq@g-soft.com.cn>
VOLUME /tmp
ARG JAR_FILE
ARG PROJECT_NAME
ENV name=${PROJECT_NAME}.jar TINI_VERSION=v0.18.0 TZ=Asia/Shanghai
COPY ${JAR_FILE} ${PROJECT_NAME}.jar
RUN apk add --no-cache tini && apk add tzdata && ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ENTRYPOINT ["/sbin/tini", "--"]
CMD java -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -XX:MaxRAMFraction=2 -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=docker -jar /$name
EXPOSE 80