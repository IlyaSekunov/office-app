plugins {
    alias(libs.plugins.officeapp.android.library)
    alias(libs.plugins.officeapp.android.hilt)
}

android {
    namespace = "ru.ilyasekunov.officeapp.data.office"
}

dependencies {
    implementation(projects.data.model)
    implementation(projects.core.network)
    implementation(projects.core.common)

    implementation(libs.retrofit)
}