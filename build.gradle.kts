plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.androidx.navigation.safeargs) apply false
    alias(libs.plugins.org.jetbrains.kotlin.kapt) apply false
    id("com.google.dagger.hilt.android") version "2.51.1" apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
}

