plugins {
    id("common-conventions")
    id("io.ktor.plugin") version "2.3.7"
}


val appGroup: String by rootProject
val appVersion: String by rootProject

group = appGroup
version = appVersion

application {
    mainClass.set("io.qtd.fungpt.infra.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

ktor {
    fatJar {
        archiveFileName.set("server-all.jar")
    }
}


dependencies {
    implementation(project(":app:common:adapter"))
    implementation(project(":app:auth:adapter"))
    implementation(project(":app:user:adapter"))
}
