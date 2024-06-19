import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.officeapp.android.library)
    alias(libs.plugins.officeapp.android.hilt)
}

private val imgurApiClientId: String by lazy {
    gradleLocalProperties(rootDir, providers).getProperty("IMGUR_CLIENT_ID").orEmpty()
}

android {
    namespace = "ru.ilyasekunov.officeapp.data.images"

    defaultConfig {
        buildConfigField("String", "IMGUR_CLIENT_ID", "\"$imgurApiClientId\"")
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(projects.data.model)
    implementation(projects.data.dto)
    implementation(projects.core.network)
    implementation(projects.core.common)
    implementation(projects.data.util)

    implementation(libs.retrofit)
    implementation(libs.converter.gson)
}