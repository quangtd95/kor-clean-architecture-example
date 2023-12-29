import Dependencies.Versions.EXPOSED_VERSION
import Dependencies.Versions.HIKARI_CP_VERSION
import Dependencies.Versions.JBCRYPT_VERSION
import Dependencies.Versions.KOIN_VERSION
import Dependencies.Versions.KTOR_SWAGGER_UI_VERSION
import Dependencies.Versions.KTOR_VERSION
import Dependencies.Versions.KT_ES_VERSION
import Dependencies.Versions.LOGBACK_VERSION
import Dependencies.Versions.OPENAI_CLIENT_VERSION
import Dependencies.Versions.POSTGRESQL_VERSION

plugins {
    kotlin("jvm") version "1.9.21"
    id("io.ktor.plugin") version "2.3.7"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.21"
}

group = AppConfig.GROUP
version = AppConfig.VERSION

application {
    mainClass.set("io.zinu.migaku.infra.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
    maven("https://maven.tryformation.com/releases") {
        content {
            includeGroup("com.jillesvangurp")
        }
    }

}

dependencies {
    // ------------------------ktor------------------------
    api("io.ktor:ktor-server-core-jvm:$KTOR_VERSION")
    api("io.ktor:ktor-server-resources:$KTOR_VERSION")
    api("io.ktor:ktor-server-netty-jvm:$KTOR_VERSION")
    api("io.ktor:ktor-server-content-negotiation:$KTOR_VERSION")
    api("io.ktor:ktor-serialization-jackson:$KTOR_VERSION")
    api("io.ktor:ktor-server-auth-jvm:$KTOR_VERSION")
    api("io.ktor:ktor-server-auth-jwt-jvm:$KTOR_VERSION")
    api("io.ktor:ktor-server-default-headers-jvm:$KTOR_VERSION")
    api("io.ktor:ktor-server-cors-jvm:$KTOR_VERSION")
    api("io.ktor:ktor-server-call-logging-jvm:$KTOR_VERSION")
    api("io.ktor:ktor-server-host-common-jvm:$KTOR_VERSION")
    api("io.ktor:ktor-server-status-pages-jvm:$KTOR_VERSION")
    api("io.ktor:ktor-client-okhttp:$KTOR_VERSION")

    // ------------------------logback------------------------
    api("ch.qos.logback:logback-classic:$LOGBACK_VERSION")

    // ------------------------exposed------------------------
    api("org.jetbrains.exposed:exposed-core:$EXPOSED_VERSION")
    api("org.jetbrains.exposed:exposed-dao:$EXPOSED_VERSION")
    api("org.jetbrains.exposed:exposed-jdbc:$EXPOSED_VERSION")
    api("org.jetbrains.exposed:exposed-java-time:$EXPOSED_VERSION")

    // ------------------------hikari------------------------
    api("com.zaxxer:HikariCP:$HIKARI_CP_VERSION")

    // ------------------------postgresql------------------------
    api("org.postgresql:postgresql:$POSTGRESQL_VERSION")

    // ------------------------jbcrypt------------------------
    api("org.mindrot:jbcrypt:$JBCRYPT_VERSION")

    // ------------------------openai------------------------
    api("com.aallam.openai:openai-client:$OPENAI_CLIENT_VERSION")

    // ------------------------koin------------------------
    api("io.insert-koin:koin-ktor:$KOIN_VERSION")
    api("io.insert-koin:koin-logger-slf4j:$KOIN_VERSION")

    // ------------------------ktor-swagger-ui------------------------
    api("io.github.smiley4:ktor-swagger-ui:$KTOR_SWAGGER_UI_VERSION")

    // ------------------------kotlin-search-client------------------------
    api("com.jillesvangurp:search-client:$KT_ES_VERSION")
}
