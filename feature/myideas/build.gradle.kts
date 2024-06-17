plugins {
    alias(libs.plugins.officeapp.android.feature)
    alias(libs.plugins.officeapp.android.library.compose)
}

android {
    namespace = "ru.ilyasekunov.officeapp.feature.ideaauthor"
}

dependencies {
    implementation(projects.data.posts)
    implementation(projects.feature.home)
    implementation(projects.feature.suggestidea)
    implementation(projects.feature.favouriteideas)
}