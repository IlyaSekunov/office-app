import ru.ilyasekunov.convention.ru.ilyasekunov.officeapp.OfficeBuildType

plugins {
    alias(libs.plugins.officeapp.android.application)
    alias(libs.plugins.officeapp.android.application.flavors)
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
        debug {
            applicationIdSuffix = OfficeBuildType.DEBUG.applicationIdSuffix
        }
        release {
            applicationIdSuffix = OfficeBuildType.RELEASE.applicationIdSuffix
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
    implementation(projects.feature.auth)
    implementation(projects.feature.user.profile)
    implementation(projects.feature.user.manage)
    implementation(projects.feature.home)
    implementation(projects.feature.suggestidea)
    implementation(projects.feature.favouriteideas)
    implementation(projects.feature.filters)
    implementation(projects.feature.editidea)
    implementation(projects.feature.ideaauthor)
    implementation(projects.feature.ideadetails)
    implementation(projects.feature.myideas)
    implementation(projects.feature.office)

    implementation(projects.core.ui)
    implementation(projects.core.navigation)

    implementation(libs.activity.compose)
    implementation(libs.androidx.ktx)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)

    // Material
    implementation(libs.androidx.material3)
    runtimeOnly(libs.androidx.material3)

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