plugins {
    alias(libs.plugins.officeapp.android.library)
}

android {
    namespace = "ru.ilyasekunov.officeapp.data.pager"
}

dependencies {
    api(libs.androidx.paging.compose)
}