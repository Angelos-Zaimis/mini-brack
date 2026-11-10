// A deployable Spring Boot service: java conventions + Boot plugin + Boot BOM.

import org.springframework.boot.gradle.plugin.SpringBootPlugin
import org.springframework.boot.gradle.tasks.run.BootRun

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

// bootRun uses the "local" profile unless SPRING_PROFILES_ACTIVE (or similar) says otherwise.
// "default" instead of "active" so any explicitly active profile still wins.
// Only affects bootRun; the packaged jar never sees this.
tasks.named<BootRun>("bootRun") {
    systemProperty("spring.profiles.default", "local")
}
