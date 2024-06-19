plugins {
    alias(libs.plugins.officeapp.android.feature)
    alias(libs.plugins.officeapp.android.library.compose)
}

android {
    namespace = "ru.ilyasekunov.officeapp.feature.office"
}

dependencies {
    implementation(projects.core.network)
    implementation(projects.data.posts)
    implementation(projects.data.auth)
    implementation(projects.data.author)
    implementation(projects.feature.home)
    implementation(projects.feature.user.profile)
}