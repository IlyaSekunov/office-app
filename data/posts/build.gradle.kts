plugins {
    alias(libs.plugins.officeapp.android.library)
    alias(libs.plugins.officeapp.android.hilt)
}

android {
    namespace = "ru.ilyasekunov.officeapp.data.posts"
}

dependencies {
    implementation(projects.data.model)
    implementation(projects.data.dto)
    implementation(projects.core.network)
    implementation(projects.core.common)
    implementation(projects.data.pager)
    implementation(projects.data.test)

    implementation(libs.retrofit)
}