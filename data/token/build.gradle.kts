plugins {
    alias(libs.plugins.officeapp.android.library)
    alias(libs.plugins.officeapp.android.hilt)
}

android {
    namespace = "ru.ilyasekunov.officeapp.data.token"
}

dependencies {
    implementation(projects.core.common)

    implementation(libs.androidx.datastore.preferences)
}