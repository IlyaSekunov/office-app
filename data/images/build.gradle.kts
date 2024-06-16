plugins {
    alias(libs.plugins.officeapp.android.library)
    alias(libs.plugins.officeapp.android.hilt)
}

android {
    namespace = "ru.ilyasekunov.officeapp.data.images"
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