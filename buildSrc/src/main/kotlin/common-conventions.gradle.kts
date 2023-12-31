plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}


repositories {
    mavenCentral()
    maven("https://maven.tryformation.com/releases") {
        content {
            includeGroup("com.jillesvangurp")
        }
    }

}
