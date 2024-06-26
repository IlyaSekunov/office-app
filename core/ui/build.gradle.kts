plugins {
    alias(libs.plugins.officeapp.android.library)
    alias(libs.plugins.officeapp.android.library.compose)
}

android {
    namespace = "ru.ilyasekunov.officeapp.core.ui"
}

dependencies {
    api(libs.androidx.material3)
    api(libs.androidx.compose.runtime)
    api(libs.androidx.compose.foundation)
    api(libs.coil.compose)
    api(libs.lottie.compose)
    api(libs.androidx.paging.compose)
    api(libs.compose.shimmer)

    implementation(projects.data.util)
    implementation(projects.core.navigation)
    implementation(projects.data.model)
    implementation(projects.data.validation)
    implementation(projects.core.permissions.storage)
}
