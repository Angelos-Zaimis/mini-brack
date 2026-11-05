plugins {
    `kotlin-dsl`
}

dependencies {
    // Third-party plugins applied by our convention plugins. Versions come from the catalog.
    implementation(libs.spring.boot.gradle.plugin)
    implementation(libs.spotless.gradle.plugin)
}
