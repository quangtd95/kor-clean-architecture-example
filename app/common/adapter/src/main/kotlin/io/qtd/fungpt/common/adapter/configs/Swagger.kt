package io.qtd.fungpt.common.adapter.configs

import io.github.smiley4.ktorswaggerui.data.AuthScheme
import io.github.smiley4.ktorswaggerui.data.AuthType
import io.github.smiley4.ktorswaggerui.dsl.OpenApiRoute
import io.github.smiley4.ktorswaggerui.dsl.PluginConfigDsl

const val SWAGGER_SECURITY_SCHEMA = "BearerJWTAuth"
typealias ApiDoc = OpenApiRoute.() -> Unit

fun PluginConfigDsl.configSwagger() {
    info {
        title = "Fun-GPT"
        version = "latest"
        description = "Fun-GPT API"
        termsOfService = "http://www.example.com/terms"
        contact {
            name = "Fun-GPT Support"
            url = "https://github.com/quangtd95"
            email = "quang.td95@gmail.com"
        }
        license {
            name = "MIT"
            url = ""
        }
    }
    server {
        url = "http://localhost:8989"
        description = "Development server"
    }
    server {
        url = "https://fungpt.com/"
        description = "Production server"
    }
    securityScheme(SWAGGER_SECURITY_SCHEMA) {
        type = AuthType.HTTP
        scheme = AuthScheme.BEARER
        bearerFormat = "jwt"
    }
}