#!/bin/bash

set -o nounset
set -o errexit
set -o pipefail

###### Maven ######
${MVN} deploy -DskipTests -Psign
