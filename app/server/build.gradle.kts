plugins {
    kotlin("jvm") version "1.9.21"
    id("io.ktor.plugin") version "2.3.7"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.21"
}
val appGroup: String by rootProject
val appVersion: String by rootProject

group = appGroup
version = appVersion

application {
    mainClass.set("io.zinu.migaku.infra.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
    maven("https://maven.tryformation.com/releases") {
        content {
            includeGroup("com.jillesvangurp")
        }
    }

}

dependencies {
    implementation(project(":app:common"))
    implementation(project(":app:auth:core"))
    implementation(project(":app:auth:adapter"))
    implementation(project(":app:user:core"))
    implementation(project(":app:user:adapter"))
}
