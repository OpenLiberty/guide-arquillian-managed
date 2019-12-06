#!/bin/bash
set -euxo pipefail

mvn clean package liberty:create liberty:install-feature liberty:deploy

# Run configure arquillian goal to create the arquillian.xml file
mvn liberty:configure-arquillian

mvn liberty:start
mvn failsafe:integration-test

mvn liberty:stop
mvn failsafe:verify
