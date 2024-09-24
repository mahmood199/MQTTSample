pluginManagement {
    repositories {
        mavenCentral()
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        gradlePluginPortal()
        maven("https://repo.eclipse.org/content/repositories/paho-releases/")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        google()
        maven("https://repo.eclipse.org/content/repositories/paho-releases/")
    }
}

rootProject.name = "MQTTSample"
include(":app")
include(":mqttservice")
