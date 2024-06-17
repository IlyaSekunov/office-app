plugins {
    alias(libs.plugins.officeapp.android.feature)
    alias(libs.plugins.officeapp.android.library.compose)
}

android {
    namespace = "ru.ilyasekunov.officeapp.feature.ideaauthor"
}

dependencies {
    implementation(projects.data.posts)
    implementation(projects.data.auth)
    implementation(projects.data.images)
    implementation(projects.data.comments)
    implementation(projects.data.dto)
    implementation(projects.data.util)
    implementation(projects.core.network)
    implementation(projects.feature.user.profile)
    implementation(projects.feature.home)
}