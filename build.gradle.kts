// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        jcenter()
        mavenCentral()
        maven {
            url = uri("https://maven.fabric.io/public")
        }
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.2.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.10")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.28-alpha")
        classpath("com.google.gms:google-services:4.3.5")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.4.1")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.3.3")
        classpath("com.google.firebase:perf-plugin:1.3.4")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("http://mobilesdk.useinsider.com/android") }
//        maven() {
//            name = "TekoMaven"
//            url = uri("https://maven.pkg.github.com/teko-vn/vnshop-android")
//
//            credentials {
//                username "GithubPackageReader"
//                password "36b56bb88ffa35820310418ed5dc597d8151afbc"
////                password "1e2a7e2164769b48b0377ace71f594d02fe8a12f"
//            }
//        }
        maven() {
            name = "TekoMaven"
            url = uri("https://maven.pkg.github.com/teko-vn/vnshop-android")

            credentials {
                username = "GithubPackageReader"
                password = "36b56bb88ffa35820310418ed5dc597d8151afbc"
            }
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
