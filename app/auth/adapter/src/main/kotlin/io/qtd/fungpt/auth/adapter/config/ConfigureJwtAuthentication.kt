package io.qtd.fungpt.auth.adapter.config

import com.auth0.jwt.interfaces.JWTVerifier
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.qtd.fungpt.auth.core.config.JwtConfig
import io.qtd.fungpt.common.adapter.base.BaseResponse
import io.qtd.fungpt.common.adapter.utils.Constants.JWT_AUTH

fun AuthenticationConfig.configureJwtAuthentication(
    env: JwtConfig, tokenVerifier: JWTVerifier
) {
    jwt(JWT_AUTH) {
        verifier(tokenVerifier)
        realm = env.realm

        validate {
            UserIdPrincipal(it.payload.getClaim("id").asString())
        }

        challenge { defaultScheme, realm ->
            val error = "Access token is invalid: realm=$realm, defaultScheme=$defaultScheme"
            val r = BaseResponse.authenticationError(error)
            call.respond(HttpStatusCode.fromValue(r.status), r)
        }
    }
}
