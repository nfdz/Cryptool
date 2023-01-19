#!/bin/bash -xe

./gradlew :androidApp:testDebugUnitTest --no-daemon
./gradlew :androidUI:testDebugUnitTest --no-daemon
./gradlew :shared:testDebugUnitTest --no-daemon
