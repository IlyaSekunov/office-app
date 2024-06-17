pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "OfficeApp"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":app")
include(":data:dto")
include(":data:auth")
include(":data:model")
include(":data:token")
include(":core:network")
include(":core:common")
include(":data:author")
include(":data:pager")
include(":data:comments")
include(":data:images")
include(":data:util")
include(":data:office")
include(":data:posts")
include(":data:user")
include(":feature")
include(":feature:auth")
include(":core:ui")
include(":data:validation")
include(":core:navigation")
include(":feature:editidea")
include(":feature:suggestidea")
include(":feature:favouriteideas")
include(":feature:filters")
include(":feature:home")
include(":feature:user")
include(":feature:user:profile")
