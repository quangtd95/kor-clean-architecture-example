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
    archiveBaseName.set("user-core")
}

dependencies {
    implementation(project(":app:common:core"))
    implementation(project(":app:auth:core"))
}
