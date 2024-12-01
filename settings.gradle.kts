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
        maven{ setUrl("https://jitpack.io")}
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven{
            setUrl("https://jitpack.io")
            credentials { username = "jp_smmimeejq57smiu7kflrpv5i2d" }
        }
    }
}

rootProject.name = "Ansar"
include(":app")
include(":AnsarLibrary")
