// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.52") {
            exclude(group = "com.squareup", module = "javapoet")
        }
        classpath("com.squareup:javapoet:1.13.0")
        classpath("com.google.devtools.ksp:symbol-processing-gradle-plugin:2.0.21-1.0.25")
    }
}

allprojects {
    configurations.configureEach {
        resolutionStrategy.force("com.squareup:javapoet:1.13.0")
    }
}
