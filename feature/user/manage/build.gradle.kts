plugins {
    alias(libs.plugins.officeapp.android.feature)
    alias(libs.plugins.officeapp.android.library.compose)
}

android {
    namespace = "ru.ilyasekunov.officeapp.feature.user.manage"
}

dependencies {
    implementation(projects.data.auth)
    implementation(projects.data.user)
    implementation(projects.data.office)
    implementation(projects.data.images)
    implementation(projects.data.validation)
    implementation(projects.data.dto)
    implementation(projects.core.network)
    implementation(projects.feature.auth)
    implementation(projects.feature.user.profile)
}