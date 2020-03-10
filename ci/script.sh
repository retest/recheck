#!/bin/bash

set -o nounset
set -o errexit
set -o pipefail

###### Maven ######
# Compile with JDK 8
mvn ${MVN_ARGS} clean package -DskipTests

# Test with JDK 13
wget --quiet https://github.com/sormuras/bach/raw/master/install-jdk.sh && . ./install-jdk.sh -F 13

if [ ${TRAVIS_SECURE_ENV_VARS} = "true" ]; then
    mvn ${MVN_ARGS} verify sonar:sonar -Pcoverage
else
    mvn ${MVN_ARGS} verify
fi
