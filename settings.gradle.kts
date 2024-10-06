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
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "PushWords"
include(":app")
include(":feature:home")
include(":feature:wordlist")
include(":feature:settings")
include(":feature:addword")
include(":feature:editword")
include(":feature:progress")
include(":navigation")
include(":data:di")
include(":data:model")
include(":common")
