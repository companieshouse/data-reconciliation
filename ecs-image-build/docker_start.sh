#!/bin/bash
#
# Start script for data-reconciliation




PORT=8080
exec java -jar -Dserver.port="${PORT}" "data-reconciliation.jar"
