plugins {
    alias(libs.plugins.officeapp.android.library)
    alias(libs.plugins.officeapp.android.hilt)
}

android {
    namespace = "ru.ilyasekunov.officeapp.core.network"
}

dependencies {
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
}