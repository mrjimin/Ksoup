plugins {
    alias(libs.plugins.kotlin.jvm)
    id("org.jetbrains.dokka") version "2.3.0-Beta"
}

group = "com.github.mrjimin.ksoup"
version = "1.0.2"

repositories {
    mavenCentral()
}

dependencies {
    api(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    api(libs.jsoup)
    implementation(libs.logback.classic)

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}