plugins {
    id("com.android.application")
}

android {
    compileSdkVersion(28)

    defaultConfig {
        applicationId = "com.termux.styling"
        minSdkVersion(21)
        targetSdkVersion(28)
        versionCode = 24
        versionName = "0.24"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.txt")
        }
    }

    compileOptions {
        val javaVersion = JavaVersion.VERSION_1_8
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }
}
