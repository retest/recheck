#!/bin/bash

set -o nounset
set -o errexit
set -o pipefail

###### Maven ######
# Compile with JDK 8
${MVN} clean test-compile

# Test with JDK 11
wget --quiet https://github.com/sormuras/bach/raw/master/install-jdk.sh && . ./install-jdk.sh -F 11

if [ ${TRAVIS_SECURE_ENV_VARS} = "true" ]; then
    ${MVN} org.jacoco:jacoco-maven-plugin:prepare-agent verify sonar:sonar
else
    ${MVN} org.jacoco:jacoco-maven-plugin:prepare-agent verify
fi
