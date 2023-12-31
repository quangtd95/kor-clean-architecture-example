plugins {
    id("common-conventions")
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
