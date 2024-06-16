plugins {
    alias(libs.plugins.officeapp.android.library)
}

android {
    namespace = "ru.ilyasekunov.officeapp.core.navigation"
}

dependencies {
    api(libs.androidx.navigation.compose)
}