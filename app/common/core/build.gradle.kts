val koinVersion : String by rootProject
val logbackVersion : String by rootProject


plugins {
    id("common-conventions")
}


tasks.jar {
    archiveBaseName.set("common-core")
}

dependencies {
    // ------------------------logback------------------------
    api("ch.qos.logback:logback-classic:$logbackVersion")

    // ------------------------koin------------------------
    api("io.insert-koin:koin-ktor:$koinVersion")
    api("io.insert-koin:koin-logger-slf4j:$koinVersion")
}
