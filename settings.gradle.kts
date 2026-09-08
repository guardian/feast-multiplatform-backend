pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "feast-multiplatform-backend"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":api")
include(":graphql")
