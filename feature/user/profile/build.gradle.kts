plugins {
    alias(libs.plugins.officeapp.android.feature)
    alias(libs.plugins.officeapp.android.library.compose)
}

android {
    namespace = "ru.ilyasekunov.officeapp.feature.user.profile"
}

dependencies {
    implementation(projects.data.auth)
    implementation(projects.core.network)
    implementation(projects.feature.auth)
}