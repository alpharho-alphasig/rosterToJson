plugins {
    alias(libs.plugins.kotlin)
}

group = "alpha.sig.alpha.rho"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(libs.kotlin.reflect)
    implementation(libs.jackson.kotlin)
    implementation(libs.jackson.jsr310)
    implementation(libs.jackson.databind)
    implementation(libs.poi)
    implementation(libs.poi.ooxml)
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}
