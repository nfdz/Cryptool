#!/bin/bash -xe

./gradlew :androidApp:testDebugUnitTest
./gradlew :androidUI:testDebugUnitTest
./gradlew :shared:testDebugUnitTest
