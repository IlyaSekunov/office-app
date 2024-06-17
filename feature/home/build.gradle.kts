plugins {
    alias(libs.plugins.officeapp.android.feature)
    alias(libs.plugins.officeapp.android.library.compose)
}

android {
    namespace = "ru.ilyasekunov.officeapp.feature.home"
}

dependencies {
    implementation(projects.data.auth)
    implementation(projects.data.posts)
    implementation(projects.data.dto)
    implementation(projects.data.util)
    implementation(projects.core.network)
    implementation(projects.feature.filters)
    implementation(projects.feature.user.profile)
}