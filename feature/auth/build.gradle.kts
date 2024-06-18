plugins {
    alias(libs.plugins.officeapp.android.feature)
    alias(libs.plugins.officeapp.android.library.compose)
}

android {
    namespace = "ru.ilyasekunov.officeapp.feature.auth"
}

dependencies {
    api(projects.data.validation)

    implementation(projects.data.auth)
    implementation(projects.data.images)
    implementation(projects.data.office)
    implementation(projects.data.dto)
    implementation(projects.core.network)
}