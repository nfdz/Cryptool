#!/bin/bash -xe

./gradlew :androidApp:assembleDebugAndroidTest
./gradlew :androidUI:assembleDebugAndroidTest
