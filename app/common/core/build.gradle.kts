val exposedVersion : String by rootProject
val hikariCpVersion : String by rootProject
val jbCryptVersion : String by rootProject
val koinVersion : String by rootProject
val ktorSwaggerUiVersion : String by rootProject
val ktorVersion : String by rootProject
val ktEsVersion : String by rootProject
val logbackVersion : String by rootProject
val openaiClientVersion : String by rootProject
val postgresqlVersion : String by rootProject


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
