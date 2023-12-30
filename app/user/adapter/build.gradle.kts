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
    implementation(project(":app:common"))
    implementation(project(":app:auth:core"))
    implementation(project(":app:user:core"))
}
