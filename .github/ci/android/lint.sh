#!/bin/bash -xe

./gradlew :androidApp:lintDebug
./gradlew :androidUI:lintDebug
./gradlew :shared:lintDebug
