plugins {
    id("com.android.application")
}

val versionCode = 24
val versionName = "0.$versionCode"

android {
    compileSdkVersion(28)

    defaultConfig {
        applicationId = "com.termux.styling"
        minSdkVersion(21)
        targetSdkVersion(28)
        this.versionCode = versionCode
        this.versionName = versionName
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

tasks.create("versionName") {
    doLast { println(versionName) }
}
