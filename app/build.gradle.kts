import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}

val keystoreProperties = Properties()
val keystorePropertiesFile = rootProject.file("keystore.properties")
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(keystorePropertiesFile.inputStream())
}

android {
    namespace = "com.hamsterbase.burrowui"
    compileSdk = 34

    buildFeatures {
        buildConfig = true
    }

    signingConfigs {
        create("release") {
            storeFile = file(
                keystoreProperties.getProperty("storeFile")
                    ?: System.getenv("KEYSTORE_FILE")
                    ?: "/dev/null"
            )
            storePassword = keystoreProperties.getProperty("storePassword")
                ?: System.getenv("KEYSTORE_PASSWORD") ?: ""
            keyAlias = keystoreProperties.getProperty("keyAlias")
                ?: System.getenv("KEY_ALIAS") ?: ""
            keyPassword = keystoreProperties.getProperty("keyPassword")
                ?: System.getenv("KEY_PASSWORD") ?: ""
        }
    }

    defaultConfig {
        applicationId = "com.hamsterbase.burrowui"
        minSdk = 24
        targetSdk = 34
        versionCode = 3
        versionName = "2.0.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
        }
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    applicationVariants.all {
        val variant = this
        variant.outputs
            .map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
            .forEach { output ->
                val project = "burrow-ui"
                val s = "-"
                val buildType = variant.buildType.name
                val version = variant.versionName

                val newApkName = "${project}${s}${buildType}${s}${version}.apk"
                output.outputFileName = newApkName
            }
    }
}

dependencies {
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
