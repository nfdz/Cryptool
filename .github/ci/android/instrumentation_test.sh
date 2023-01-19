#!/bin/bash -xe

./gradlew :androidApp:connectedDebugAndroidTest
./gradlew :androidUI:connectedDebugAndroidTest
