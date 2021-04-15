#!/bin/bash

exec java ${JAVA_MEM_ARGS} -jar -Dserver.port="${PORT}" "${STARTUP_PATH:-data-reconciliation.jar}"
