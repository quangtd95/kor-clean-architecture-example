plugins {
    id("common-conventions")
}


tasks.jar {
    archiveBaseName.set("user-adapter")
}

dependencies {
    implementation(project(":app:common:adapter"))
    api(project(":app:profile:core"))
}
