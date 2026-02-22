pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
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

rootProject.name = "NadjahAcademy"

include(":app")

// Core modules
include(":core:network")
include(":core:database")
include(":core:auth")
include(":core:ui")
include(":core:domain")
include(":core:datastore")
include(":core:testing")
include(":core:analytics")

// Feature modules
include(":feature:auth")
include(":feature:home")
include(":feature:explore")
include(":feature:course")
include(":feature:lesson")
include(":feature:quiz")
include(":feature:mylearning")
include(":feature:blog")
include(":feature:profile")
include(":feature:notifications")
include(":feature:payment")
include(":feature:settings")
include(":feature:search")
include(":feature:instructor")
include(":feature:discussion")
