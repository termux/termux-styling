plugins {
    id("com.android.application")
    id("kotlin-android")
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
        getByName("debug") {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.txt")
        }
    }

    compileOptions {
        val javaVersion = JavaVersion.VERSION_1_8
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }
}

dependencies {
    implementation(embeddedKotlin("stdlib-jdk8"))
}

tasks.create("versionName") {
    doLast { println(versionName) }
}
