#!/bin/bash

PID=`ps -eaf | grep intraportal-${parent.version}.jar | grep -v grep | awk '{print $2}'`
if [[ "" !=  "$PID" ]]; then
  echo "killing $PID"
  kill -9 $PID
fi

# Get the script directory
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

# Path the the JRE to be used when starting the portal.
JAVA=/usr/bin/java

# Make logs directory
if [ ! -d "$DIR/logs/" ]; then
	mkdir "$DIR/logs/"
fi

# Start the portal
"$JAVA" -DLOG_PATH="$DIR/logs" -jar "$DIR/data/intraportal-${parent.version}.jar"

