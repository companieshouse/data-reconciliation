FROM alpine:3.13.4

ENV STARTUP_PATH=/opt/data-reconciliation/data-reconciliation.jar

RUN apk --no-cache add \
    bash \
    openjdk8

COPY data-reconciliation.jar $STARTUP_PATH
COPY start.sh /usr/local/bin/

RUN chmod 555 /usr/local/bin/start.sh

CMD ["start.sh"]
