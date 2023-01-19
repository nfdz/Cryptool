#!/bin/bash -xe

./gradlew :androidApp:lintDebug --no-daemon
./gradlew :androidUI:lintDebug --no-daemon
./gradlew :shared:lintDebug --no-daemon
