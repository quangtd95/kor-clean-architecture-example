val exposedVersion: String by rootProject
val hikariCpVersion: String by rootProject
val koinVersion: String by rootProject
val ktorSwaggerUiVersion: String by rootProject
val ktorVersion: String by rootProject
val ktEsVersion: String by rootProject
val logbackVersion: String by rootProject
val openaiClientVersion: String by rootProject
val postgresqlVersion: String by rootProject


plugins {
    id("common-conventions")
}

// set name of jar file output
tasks.jar {
    archiveBaseName.set("common-adapter")
}

dependencies {
    api(project(":app:common:core"))
    // ------------------------ktor------------------------
    api("io.ktor:ktor-server-core-jvm:$ktorVersion")
    api("io.ktor:ktor-server-resources:$ktorVersion")
    api("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    api("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    api("io.ktor:ktor-serialization-jackson:$ktorVersion")
    api("io.ktor:ktor-server-default-headers-jvm:$ktorVersion")
    api("io.ktor:ktor-server-cors-jvm:$ktorVersion")
    api("io.ktor:ktor-server-call-logging-jvm:$ktorVersion")
    api("io.ktor:ktor-server-host-common-jvm:$ktorVersion")
    api("io.ktor:ktor-server-status-pages-jvm:$ktorVersion")
    api("io.ktor:ktor-client-okhttp:$ktorVersion")
    api("io.ktor:ktor-server-call-id:$ktorVersion")
    api("io.ktor:ktor-server-auth-jvm:$ktorVersion")
    api("io.ktor:ktor-server-auth-jwt-jvm:$ktorVersion")

    // ------------------------exposed------------------------
    api("org.jetbrains.exposed:exposed-core:$exposedVersion")
    api("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    api("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    api("org.jetbrains.exposed:exposed-java-time:$exposedVersion")

    // ------------------------hikari------------------------
    api("com.zaxxer:HikariCP:$hikariCpVersion")

    // ------------------------postgresql------------------------
    api("org.postgresql:postgresql:$postgresqlVersion")


    // ------------------------openai------------------------
    api("com.aallam.openai:openai-client:$openaiClientVersion")

    // ------------------------ktor-swagger-ui------------------------
    api("io.github.smiley4:ktor-swagger-ui:$ktorSwaggerUiVersion")

    // ------------------------kotlin-search-client------------------------
    api("com.jillesvangurp:search-client:$ktEsVersion")
}
