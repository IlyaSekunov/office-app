plugins {
    alias(libs.plugins.officeapp.jvm.library)
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation(libs.assertk)
}