#!/bin/bash
set -euxo pipefail

# Test without liberty:deploy
mvn -q clean package
mvn -q liberty:create liberty:install-feature
mvn -q liberty:configure-arquillian
mvn failsafe:integration-test
mvn failsafe:verify

# Test with liberty:deploy
mvn -q clean package
mvn -q liberty:create liberty:install-feature liberty:deploy
mvn -q liberty:configure-arquillian
mvn failsafe:integration-test
mvn failsafe:verify
