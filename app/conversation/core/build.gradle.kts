plugins {
    id("common-conventions")
}

tasks.jar {
    archiveBaseName.set("conversation-core")
}

dependencies {
    implementation(project(":app:common:core"))
    implementation(project(":app:auth:core"))
}
