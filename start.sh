#!/bin/bash

exec java ${JAVA_MEM_ARGS} -jar -Dserver.port="${PORT}" -XX:MaxRAMPercentage=80 "${STARTUP_PATH:-data-reconciliation.jar}"
