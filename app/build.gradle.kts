plugins {
    alias(libs.plugins.officeapp.android.application)
    alias(libs.plugins.officeapp.android.application.compose)
    alias(libs.plugins.officeapp.android.hilt)
}

android {
    namespace = "ru.ilyasekunov.officeapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "ru.ilyasekunov.officeapp"
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.ktx)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)

    // Lottie
    implementation(libs.lottie.compose)

    // Paging library
    implementation(libs.androidx.paging.compose)

    // Shimmer effect
    implementation(libs.compose.shimmer)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // Material
    implementation(libs.androidx.material3)
    runtimeOnly(libs.androidx.material3)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // Coil
    implementation(libs.coil.compose)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    ksp(libs.hilt.android.compiler)

    // JUnit
    testImplementation(libs.junit)

    // Mockito
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    implementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}