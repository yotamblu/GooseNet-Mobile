// File: build.gradle.kts (root project)

plugins {
    // No plugins here usually for AGP; use buildscript below
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.9.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.10") // Use your Kotlin version here
    }
}

// ✅ Correct extra property setup
extra.set("geckoviewChannel", "nightly")
extra.set("geckoviewVersion", "127.0.20240618110440")


allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") } // ✅ This is required for MPAndroidChart
        maven {
            url = uri("https://maven.mozilla.org/maven2/")
        }
    }
}
