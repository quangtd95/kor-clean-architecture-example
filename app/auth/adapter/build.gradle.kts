val jbCryptVersion: String by rootProject
val ktorVersion: String by rootProject
plugins {
    id("common-conventions")
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
