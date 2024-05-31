#!/bin/bash

PROJECT_VERSION=$(mvn -q -Dexec.executable=echo -Dexec.args='${project.version}' --non-recursive exec:exec)

docker build -t ap/widgetapp:"$PROJECT_VERSION" .
