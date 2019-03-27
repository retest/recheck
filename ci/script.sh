#!/bin/bash

set -o nounset
set -o errexit
set -o pipefail

###### Maven ######
if [ ${TRAVIS_SECURE_ENV_VARS} = "true" ]; then
    ${MVN} clean org.jacoco:jacoco-maven-plugin:prepare-agent verify sonar:sonar
else
    ${MVN} clean org.jacoco:jacoco-maven-plugin:prepare-agent verify
fi
