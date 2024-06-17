plugins {
    alias(libs.plugins.officeapp.android.feature)
    alias(libs.plugins.officeapp.android.library.compose)
}

android {
    namespace = "ru.ilyasekunov.officeapp.feature.filters"
}

dependencies {
    implementation(projects.feature.user.profile)
    implementation(projects.data.auth)
    implementation(projects.core.network)
    implementation(projects.data.dto)
}