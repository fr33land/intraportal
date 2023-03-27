#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

JAVA=/usr/bin/java
"$JAVA" -jar -Duser.timezone="UTC" -Ddb.url=jdbc:postgresql://localhost:5432/intraportaldb -Ddb.username=intraportal -Ddb.password=123 "$DIR/data/db-migration.jar"