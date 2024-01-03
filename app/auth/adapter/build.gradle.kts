val jbCryptVersion: String by rootProject
val ktorVersion: String by rootProject
plugins {
    id("common-conventions")
}


tasks.jar {
    archiveBaseName.set("auth-adapter")
}

dependencies {
    implementation(project(":app:common:adapter"))
    api(project(":app:auth:core"))

    // ------------------------jbcrypt------------------------
    api("org.mindrot:jbcrypt:$jbCryptVersion")
}
