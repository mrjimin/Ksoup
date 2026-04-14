plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
}

group = "com.github.mrjimin.ksoup"
version = "1.0.1"

application {
    mainClass = "com.github.mrjimin.ksoup.MainKt"
}

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