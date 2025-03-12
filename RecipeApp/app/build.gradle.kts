plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    alias(libs.plugins.kotlinAndroidKsp)
    // alias(libs.plugins.hiltAndroid)
    id("com.google.dagger.hilt.android")

}

android {
    namespace = "com.knyazev.recipeapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.knyazev.recipeapp"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    buildToolsVersion = "35.0.0"
    configurations.all {
        resolutionStrategy {
            force("org.jetbrains:annotations:23.0.0")
            exclude(group = "com.intellij", module = "annotations")
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.room.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.androidx.navigation.compose)  //Навигация для экранов приложения
    implementation (libs.material) // Для использования Material Design
    implementation (libs.androidx.material.icons.extended) // Для использования Material Icons
    implementation(libs.androidx.animation) // Для анимации

    implementation (libs.gson) // Для сериализации/десериализации данных
    implementation(libs.androidx.room.runtime)  // или последняя версия
    ksp(libs.androidx.room.compiler) // Используем KSP для Kotlin Symbol Processing

    implementation(libs.androidx.lifecycle.livedata.ktx) // LiveData для асинхронных операций Room
    implementation(libs.androidx.lifecycle.viewmodel.ktx) // ViewModel для управления данными

    implementation(libs.hilt.android) // Для использования Hilt
    ksp(libs.hilt.compiler) // Используем KSP для Kotlin Symbol Processing
    //implementation("com.google.dagger:hilt-android:2.51.1")
    //ksp("com.google.dagger:hilt-android-compiler:2.51.1")
    implementation (libs.androidx.hilt.navigation.fragment)
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("io.coil-kt:coil-compose:2.6.0")


}