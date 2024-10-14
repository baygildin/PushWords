plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.androidx.navigation.safeargs)
    alias(libs.plugins.org.jetbrains.kotlin.kapt)
    id("com.google.dagger.hilt.android")



}

android {
    namespace = "com.sbaygildin.pushwords"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.sbaygildin.pushwords"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        vectorDrawables {
            useSupportLibrary = true
        }
        buildFeatures {
            viewBinding = true
            dataBinding = true
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.hilt.common)
    implementation(libs.androidx.hilt.work)
    implementation("androidx.core:core-splashscreen:1.0.0")
    val hilt_version = "2.51.1"
    implementation("com.google.dagger:hilt-android:$hilt_version")
    kapt("com.google.dagger:hilt-android-compiler:$hilt_version")
    implementation("androidx.hilt:hilt-work:1.0.0")
    kapt("androidx.hilt:hilt-compiler:1.2.0")
    implementation(project(":data:model"))
    implementation(project(":data:di"))
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(project(":feature:home"))
    implementation(project(":feature:wordlist"))
    implementation(project(":feature:settings"))
    implementation(project(":feature:addword"))
    implementation(project(":feature:editword"))
    implementation(project(":feature:progress"))
    implementation(project(":navigation"))
    val coroutines_version = "1.9.0"
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines_version")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines_version")
    val work_version = "2.9.1"
    implementation("androidx.work:work-runtime-ktx:$work_version")
}

kapt {
    correctErrorTypes = true
}
apply(plugin = "kotlin-kapt")

