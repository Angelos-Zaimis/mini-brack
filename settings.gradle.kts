pluginManagement {
    // Convention plugins (minibrack.*) live in an included build.
    includeBuild("build-logic")
}

dependencyResolutionManagement {
    // Repositories are declared once, here. Modules may not add their own.
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        mavenCentral()
    }
}

rootProject.name = "minibrack"

include(":services:catalog-service")
