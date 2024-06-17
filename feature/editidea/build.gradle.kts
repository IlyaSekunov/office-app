plugins {
    alias(libs.plugins.officeapp.android.feature)
    alias(libs.plugins.officeapp.android.library.compose)
}

android {
    namespace = "ru.ilyasekunov.officeapp.feature.editidea"
}

dependencies {
    implementation(projects.data.posts)
    implementation(projects.data.images)
    implementation(projects.data.dto)
    implementation(projects.feature.suggestidea)
}