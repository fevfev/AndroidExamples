// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("com.google.dagger.hilt.android") version "2.52" apply false //  Hilt
    id("com.google.devtools.ksp") version "2.1.0-1.0.29" apply false // KSP (Kotlin Symbol Processing)
    //id("androidx.room") version "2.6.1" apply false // Room
}