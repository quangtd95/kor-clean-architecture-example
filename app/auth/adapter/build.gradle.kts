val jbCryptVersion: String by rootProject
val ktorVersion: String by rootProject
plugins {
    kotlin("jvm") version "1.9.21"
}


repositories {
    mavenCentral()
    maven("https://maven.tryformation.com/releases") {
        content {
            includeGroup("com.jillesvangurp")
        }
    }

}

tasks.jar {
    archiveBaseName.set("auth-adapter")
}

dependencies {
    implementation(project(":app:common"))
    implementation(project(":app:auth:core"))

    // ------------------------ktor------------------------
    api("io.ktor:ktor-server-auth-jvm:$ktorVersion")
    api("io.ktor:ktor-server-auth-jwt-jvm:$ktorVersion")

    // ------------------------jbcrypt------------------------
    api("org.mindrot:jbcrypt:$jbCryptVersion")
}
