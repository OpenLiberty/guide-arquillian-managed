#!/bin/bash
set -euxo pipefail

# Test without liberty:deploy
mvn clean package
mvn liberty:create liberty:install-feature
mvn liberty:configure-arquillian
mvn failsafe:integration-test
mvn failsafe:verify

# Test with liberty:deploy
mvn clean package
mvn liberty:create liberty:install-feature liberty:deploy
mvn liberty:configure-arquillian
mvn failsafe:integration-test
mvn failsafe:verify
