// app/build.gradle.kts

plugins {
    // CRITICAL: Applies the plugins declared in the root file
    id("com.android.application") 
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.rnd.edgedetector"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.rnd.edgedetector"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        
        // NDK Configuration for C++
        ndkVersion = "26.1.10909125" // Adjust to your installed NDK version
        externalNativeBuild {
            cmake {
                cppFlags += "-std=c++20 -fexceptions -frtti"
            }
        }
    }

    // Link to the C++ CMake file
    externalNativeBuild {
        cmake {
            path = file("jni/CMakeLists.txt")
        }
    }
}

dependencies {
    // Standard dependencies
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    
    // Kotlin dependency FIX (using explicit string notation)
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.0")
}