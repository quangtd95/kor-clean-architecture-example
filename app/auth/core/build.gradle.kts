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
    archiveBaseName.set("auth-core")
}

dependencies {
    implementation(project(":app:common:core"))

}
