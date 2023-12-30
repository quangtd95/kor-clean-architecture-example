
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

dependencies {
    api(project(":app:common:core"))
    api(project(":app:common:adapter"))
}
