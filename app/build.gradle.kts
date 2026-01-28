plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.trivia"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.trivia"
        minSdk = 24
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation("io.socket:socket.io-client:2.1.0") {
        // Sintaxis correcta para Kotlin DSL (.kts)
        exclude(group = "org.json", module = "json")
    }

    implementation ("nl.dionsegijn:konfetti-xml:2.0.4")
}