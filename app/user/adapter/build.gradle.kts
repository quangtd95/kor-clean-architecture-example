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
    archiveBaseName.set("user-adapter")
}

dependencies {
    implementation(project(":app:common"))
    implementation(project(":app:auth:core"))
    implementation(project(":app:auth:adapter"))
    implementation(project(":app:user:core"))
}
