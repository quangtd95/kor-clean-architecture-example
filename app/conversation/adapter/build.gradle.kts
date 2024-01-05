val jbCryptVersion: String by rootProject
val ktorVersion: String by rootProject
plugins {
    id("common-conventions")
}


tasks.jar {
    archiveBaseName.set("conversation-adapter")
}

dependencies {
    implementation(project(":app:common:adapter"))
    api(project(":app:conversation:core"))

}
