// File: app/build.gradle.kts

plugins {
    id("com.android.application")  // No version here!
    kotlin("android")              // Kotlin Android plugin
}

android {
    namespace = "com.example.goosenetmobile"  // Use your app package namespace here
    compileSdk = 35                          // Adjust compileSdk version

    splits {
        abi {
            isEnable = true
            reset()
            include("armeabi-v7a", "arm64-v8a") // Exclude x86, x86_64
            isUniversalApk = false // Don't bundle all ABIs in one APK
        }
    }

    defaultConfig {
        applicationId = "com.example.goosenetmobile"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        // If you use Java 8 or higher APIs
        // javaCompileOptions {
        //     annotationProcessorOptions {
        //         includeCompileClasspath = true
        //     }
        // }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }


    }
}
val geckoviewChannel: String by rootProject.extra
val geckoviewVersion: String by rootProject.extra
dependencies {
    // Gson for JSON serialization/deserialization
    implementation("com.google.code.gson:gson:2.10.1")

    // Lottie for animations
    implementation("com.airbnb.android:lottie:6.0.0")

    // Your existing dependencies, e.g.:
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.10")
    implementation ("de.hdodenhof:circleimageview:3.1.0")
    implementation ("androidx.activity:activity:1.8.1")
    implementation ("com.squareup.okhttp3:okhttp:4.12.0")
    implementation(libs.swiperefreshlayout)
    implementation(libs.browser)
    implementation(libs.junit)
    implementation(libs.monitor)
    implementation(libs.ext.junit)
    implementation("org.mozilla.geckoview:geckoview:139.0.20250523173407")
    implementation("org.mozilla.geckoview:geckoview:$geckoviewVersion-$geckoviewChannel")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    implementation(libs.constraintlayout)
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")
    implementation ("org.osmdroid:osmdroid-android:6.1.16")

}
