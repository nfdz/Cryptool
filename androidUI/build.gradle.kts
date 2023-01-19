plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    alias(libs.plugins.kotlin.serialization)
    id("dev.testify")
}

android {
    namespace = "io.github.nfdz.cryptool.ui"
    compileSdk = libs.versions.android.compile.sdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.min.sdk.get().toInt()
        targetSdk = libs.versions.android.target.sdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    val appVersionName: String by project
    buildTypes {
        forEach {
            it.buildConfigField("String", "VERSION_NAME", "\"$appVersionName\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = libs.versions.javaTarget.get()
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }

    testify {
        applicationPackageId = "io.github.nfdz.cryptool.ui"
        testPackageId = "io.github.nfdz.cryptool.ui"
    }

    lint {
        warningsAsErrors = true
    }
}

dependencies {
    implementation(project(":shared"))
    implementation(libs.bundles.androidx)
    implementation(libs.bundles.androidx.ui)
    implementation(libs.bundles.kotlin.android)
    implementation(libs.koin)
    implementation(libs.koin.android)
    implementation(libs.napier)

    testImplementation(libs.bundles.android.test.unit)
    androidTestImplementation(libs.bundles.android.test.instrumentation)
    androidTestImplementation(libs.testify.compose)
//    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
//    androidTestImplementation("androidx.compose.ui:ui-test-junit4:${libs.versions.compose}")
}

tasks.withType<Test>().configureEach {
    javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(11))
    })
}