plugins {
    alias(libs.plugins.officeapp.android.library)
    alias(libs.plugins.officeapp.android.library.compose)
}

android {
    namespace = "ru.ilyasekunov.officeapp.core.permissions.storage"
}

dependencies {
    api(libs.androidx.compose.runtime)

    implementation(libs.androidx.ktx)
    implementation(libs.activity.compose)
}
