plugins {
    alias(libs.plugins.officeapp.android.feature)
    alias(libs.plugins.officeapp.android.library.compose)
}

android {
    namespace = "ru.ilyasekunov.officeapp.feature.favouriteideas"
}

dependencies {
    implementation(projects.data.posts)
    implementation(projects.data.dto)
    implementation(projects.feature.filters)
    implementation(projects.feature.home)
}