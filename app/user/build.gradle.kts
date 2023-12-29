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
    implementation(project(":app:common"))
    implementation(project(":app:auth"))
    // ------------------------ktor------------------------
    implementation("io.ktor:ktor-server-core-jvm:$KTOR_VERSION")
    implementation("io.ktor:ktor-server-resources:$KTOR_VERSION")
    implementation("io.ktor:ktor-server-netty-jvm:$KTOR_VERSION")
    implementation("io.ktor:ktor-server-content-negotiation:$KTOR_VERSION")
    implementation("io.ktor:ktor-serialization-jackson:$KTOR_VERSION")
    implementation("io.ktor:ktor-server-auth-jvm:$KTOR_VERSION")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:$KTOR_VERSION")
    implementation("io.ktor:ktor-server-default-headers-jvm:$KTOR_VERSION")
    implementation("io.ktor:ktor-server-cors-jvm:$KTOR_VERSION")
    implementation("io.ktor:ktor-server-call-logging-jvm:$KTOR_VERSION")
    implementation("io.ktor:ktor-server-host-common-jvm:$KTOR_VERSION")
    implementation("io.ktor:ktor-server-status-pages-jvm:$KTOR_VERSION")
    implementation("io.ktor:ktor-client-okhttp:$KTOR_VERSION")

    // ------------------------exposed------------------------
    implementation("org.jetbrains.exposed:exposed-core:$EXPOSED_VERSION")
    implementation("org.jetbrains.exposed:exposed-dao:$EXPOSED_VERSION")
    implementation("org.jetbrains.exposed:exposed-jdbc:$EXPOSED_VERSION")
    implementation("org.jetbrains.exposed:exposed-java-time:$EXPOSED_VERSION")

    // ------------------------hikari------------------------
    implementation("com.zaxxer:HikariCP:$HIKARI_CP_VERSION")

    // ------------------------postgresql------------------------
    implementation("org.postgresql:postgresql:$POSTGRESQL_VERSION")

    // ------------------------jbcrypt------------------------
    implementation("org.mindrot:jbcrypt:$JBCRYPT_VERSION")

    // ------------------------openai------------------------
    implementation("com.aallam.openai:openai-client:$OPENAI_CLIENT_VERSION")

    // ------------------------koin------------------------
    implementation("io.insert-koin:koin-ktor:$KOIN_VERSION")
    implementation("io.insert-koin:koin-logger-slf4j:$KOIN_VERSION")

    // ------------------------ktor-swagger-ui------------------------
    implementation("io.github.smiley4:ktor-swagger-ui:$KTOR_SWAGGER_UI_VERSION")

    // ------------------------kotlin-search-client------------------------
    implementation("com.jillesvangurp:search-client:$KT_ES_VERSION")
}
