plugins {
    id("common-conventions")
}


tasks.jar {
    archiveBaseName.set("user-adapter")
}

dependencies {
    implementation(project(":app:common:adapter"))
    implementation(project(":app:auth:adapter"))
    api(project(":app:profile:core"))
}
