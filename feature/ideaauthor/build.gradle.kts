plugins {
    alias(libs.plugins.officeapp.android.feature)
    alias(libs.plugins.officeapp.android.library.compose)
}

android {
    namespace = "ru.ilyasekunov.officeapp.feature.ideaauthor"
}

dependencies {
    implementation(projects.data.author)
    implementation(projects.data.posts)
    implementation(projects.data.util)
    implementation(projects.core.network)
    implementation(projects.feature.user.profile)
}