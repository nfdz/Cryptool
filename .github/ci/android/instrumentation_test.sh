#!/bin/bash -xe

./gradlew :androidApp:pixel5api31DebugAndroidTest --no-daemon
./gradlew :androidUI:pixel5api31DebugAndroidTest --no-daemon
