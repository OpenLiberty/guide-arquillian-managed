#!/bin/bash
set -euxo pipefail

mvn clean package

mvn liberty:create liberty:install-feature liberty:deploy

# Run configure arquillian goal to create the arquillian.xml file
mvn liberty:configure-arquillian

# The arquillian tests manage the lifecycle of the application server, ie, start/stop
mvn failsafe:integration-test

mvn failsafe:verify
