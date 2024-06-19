plugins {
    alias(libs.plugins.officeapp.jvm.library)
}

dependencies {
    implementation(projects.data.dto)
    implementation(projects.data.model)
}