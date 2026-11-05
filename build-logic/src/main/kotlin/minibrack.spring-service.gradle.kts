// A deployable Spring Boot service: java conventions + Boot plugin + Boot BOM.

import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
    id("minibrack.java-conventions")
    id("org.springframework.boot")
}

dependencies {
    // Gradle-native BOM import instead of io.spring.dependency-management.
    val springBootBom = platform(SpringBootPlugin.BOM_COORDINATES)
    implementation(springBootBom)
    annotationProcessor(springBootBom)
    developmentOnly(springBootBom)
}
