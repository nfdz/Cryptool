import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.application")
    kotlin("android")
    alias(libs.plugins.jaredsburrows.license)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "io.github.nfdz.cryptool"
    compileSdk = libs.versions.android.compile.sdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.min.sdk.get().toInt()
        targetSdk = libs.versions.android.target.sdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        applicationId = "io.github.nfdz.cryptool"
        val appVersionCode: String by project
        val appVersionName: String by project
        versionCode = appVersionCode.toInt()
        versionName = appVersionName
    }
    val localProperties = gradleLocalProperties(rootDir)
    val signingEnabled = runCatching { localProperties.getProperty("signing.enabled").toBoolean() }.getOrElse { false }
    if (signingEnabled) {
        signingConfigs {
            create("release") {
                val ksFilePath = localProperties.getProperty("signing.keystore")
                storeFile = file(ksFilePath)
                storePassword = localProperties.getProperty("signing.keystorePw")
                keyAlias = localProperties.getProperty("signing.keyAlias")
                keyPassword = localProperties.getProperty("signing.keyPw")
            }
        }
    }
    val appCheckNewVersionOnGithub: String by project
    val appValidateCertificateOnGithub: String by project
    buildTypes {
        release {
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName(if (signingEnabled) "release" else "debug")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

            ndk {
                debugSymbolLevel = "SYMBOL_TABLE" // FULL
            }
        }
        forEach {
            it.buildConfigField("Boolean", "CHECK_NEW_VERSION_GITHUB", appCheckNewVersionOnGithub)
            it.buildConfigField("Boolean", "VALIDATE_CERTIFICATE_GITHUB", appValidateCertificateOnGithub)
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = libs.versions.javaTarget.get()
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
    licenseReport {
        generateCsvReport = false
        generateHtmlReport = false
        generateJsonReport = true
        generateTextReport = false
        copyCsvReportToAssets = false
        copyHtmlReportToAssets = false
        copyJsonReportToAssets = true
        copyTextReportToAssets = false
    }
    lint {
        warningsAsErrors = true
        disable.add("ObsoleteLintCustomCheck")
        disable.add("AndroidGradlePluginVersion")
    }
    dependenciesInfo {
        // Disables dependency metadata when building APKs.
        includeInApk = false
        // Disables dependency metadata when building Android App Bundles.
        includeInBundle = false
    }
    testOptions {
        managedDevices {
            devices {
                maybeCreate<com.android.build.api.dsl.ManagedVirtualDevice>("pixel5api31").apply {
                    device = "Pixel 5"
                    apiLevel = 31
                    systemImageSource = "aosp"
                }
            }
        }
    }
}

dependencies {
    implementation(project(":shared"))
    implementation(project(":androidUI"))
    implementation(libs.bundles.androidx)
    implementation(libs.bundles.androidx.ui)
    implementation(libs.bundles.kotlin.android)
    implementation(libs.koin)
    implementation(libs.koin.android)
    implementation(libs.napier)

    debugImplementation(libs.leakcanary)
    testImplementation(libs.bundles.android.test.unit)
    androidTestImplementation(libs.bundles.android.test.instrumentation)
    androidTestImplementation(libs.realm)
}
