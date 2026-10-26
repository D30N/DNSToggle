plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.deon.dnstoggle"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.deon.dnstoggle"
        minSdk = 28
        targetSdk = 34
        versionCode = 7
        versionName = "1.3.3"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    lint {
        checkReleaseBuilds = false
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}
