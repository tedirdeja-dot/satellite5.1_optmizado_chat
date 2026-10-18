import org.gradle.api.GradleException

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

val signingStoreFile = providers.environmentVariable("ANDROID_KEYSTORE_PATH").orNull
val signingStorePassword = providers.environmentVariable("ANDROID_KEYSTORE_PASSWORD").orNull
val signingKeyAlias = providers.environmentVariable("ANDROID_KEY_ALIAS").orNull
val signingKeyPassword = providers.environmentVariable("ANDROID_KEY_PASSWORD").orNull
val signingValues = listOf(signingStoreFile, signingStorePassword, signingKeyAlias, signingKeyPassword)
val hasAnySigningValue = signingValues.any { !it.isNullOrBlank() }
val hasCompleteSigningValues = signingValues.all { !it.isNullOrBlank() }

if (hasAnySigningValue && !hasCompleteSigningValues) {
    throw GradleException("La firma opcional está incompleta; configura los cuatro secretos de GitHub.")
}

android {
    namespace = "com.satellite.wallpaper"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.satellite.wallpaper"
        minSdk = 23
        targetSdk = 35
        versionCode = 24
        versionName = "5.0"
    }

    signingConfigs {
        if (hasCompleteSigningValues) {
            create("github") {
                storeFile = file(signingStoreFile!!)
                storePassword = signingStorePassword
                keyAlias = signingKeyAlias
                keyPassword = signingKeyPassword
            }
        }
    }

    buildTypes {
        debug {
            if (hasCompleteSigningValues) {
                signingConfig = signingConfigs.getByName("github")
            }
        }
        release {
            isMinifyEnabled = false
            if (hasCompleteSigningValues) {
                signingConfig = signingConfigs.getByName("github")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}
