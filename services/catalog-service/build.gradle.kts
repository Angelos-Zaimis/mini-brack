plugins {
    id("minibrack.spring-service")
}

dependencies {
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter.webmvc)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.flyway)
    runtimeOnly(libs.postgresql)
    runtimeOnly(libs.flyway.database.postgresql)
    developmentOnly(libs.spring.boot.docker.compose)

    testImplementation(libs.spring.boot.starter.actuator.test)
    testImplementation(libs.spring.boot.starter.webmvc.test)
    testImplementation(libs.spring.boot.testcontainers)
    testImplementation(libs.testcontainers.postgresql)
}
