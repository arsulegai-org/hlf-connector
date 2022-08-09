#!/bin/bash -e
# Script for continuous integration run.  Cleanup, start docker containers for fabric and fabric ca
# Start integration tests.

set -euo pipefail

export ORG_HYPERLEDGER_FABRIC_SDK_LOGLEVEL=TRACE
export ORG_HYPERLEDGER_FABRIC_CA_SDK_LOGLEVEL=TRACE
export ORG_HYPERLEDGER_FABRIC_SDK_LOG_EXTRALOGLEVEL=10

export ORG_HYPERLEDGER_FABRIC_SDKTEST_INTEGRATIONTESTS_TLS=false
export ORG_HYPERLEDGER_FABRIC_SDKTEST_INTEGRATIONTESTS_CA_TLS=
export ORG_HYPERLEDGER_FABRIC_SDKTEST_INTEGRATIONTESTS_CLIENT_AUTH_REQUIRED=false
export FAB_CONFIG_GEN_VERS=v2.1
export V11_IDENTITIES_ALLOWREMOVE=--cfg.identities.allowremove
export V11_AFFILIATIONS_ALLOWREMOVE=--cfg.affiliations.allowremove
export IMAGE_TAG_FABRIC=:2.1
export IMAGE_TAG_FABRIC_CA=:1.4

cd "$(dirname "$0")"
source pull-fabric-images.sh

pushd ../src/test/java/fabricSetup/ >/dev/null
docker-compose up --force-recreate -d
popd >/dev/null && cd ..

docker ps -a

export ORG_HYPERLEDGER_FABRIC_SDK_DIAGNOSTICFILEDIR=target/diagDump
export MAVEN_OPTS="-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn"
mvn clean test-compile failsafe:integration-test -Dmaven.test.failure.ignore=false

pushd src/test/java/fabricSetup/ >/dev/null
docker-compose down --volumes
