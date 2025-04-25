#!/bin/bash
#
# Start script for data-reconciliation


PORT=8080
exec java -jar -Dserver.port="${PORT}" -XX:MaxRAMPercentage=80 "data-reconciliation.jar"
