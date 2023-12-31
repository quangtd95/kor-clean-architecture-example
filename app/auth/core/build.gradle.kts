plugins {
    id("common-conventions")
}

tasks.jar {
    archiveBaseName.set("auth-core")
}

dependencies {
    implementation(project(":app:common:core"))

}
