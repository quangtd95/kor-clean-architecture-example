package io.zinu.migaku.auth.core.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.JWTVerifier
import io.zinu.migaku.auth.core.config.JwtConfig
import io.zinu.migaku.auth.core.model.CoreCredentials
import io.zinu.migaku.auth.core.model.CoreUser
import io.zinu.migaku.auth.core.usecase.TokenUsecase
import java.time.LocalDateTime
import java.time.ZoneId


class TokenService(private val jwtConfig: JwtConfig) : TokenUsecase {
    private val accessTokenAlgorithm = Algorithm.HMAC256(jwtConfig.accessTokenSecretKey)
    private val refreshTokenAlgorithm = Algorithm.HMAC512(jwtConfig.refreshTokenSecretKey)

    private val validityInS: Long = 60 * 5L // 5 minutes

    private val refreshValidityInS: Long = 60 * 60 * 24L * 30L // 30 days

    private val accessTokenVerifier = JWT
        .require(accessTokenAlgorithm)
        .withAudience(jwtConfig.audience)
        .withIssuer(jwtConfig.issuer)
        .build()!!

    private val refreshTokenVerifier = JWT
        .require(refreshTokenAlgorithm)
        .withAudience(jwtConfig.audience)
        .withIssuer(jwtConfig.issuer)
        .build()!!


    private fun createToken(user: CoreUser, expiration: LocalDateTime, algorithm: Algorithm) =
        JWT.create()
            .withSubject("Authentication")
            .withIssuer(jwtConfig.issuer)
            .withAudience(jwtConfig.audience)
            .withClaim("id", user.id)
            .withExpiresAt(expiration.atZone(ZoneId.systemDefault()).toInstant())
            .sign(algorithm)

    private fun createAccessToken(user: CoreUser, expiration: LocalDateTime) =
        createToken(user, expiration, accessTokenAlgorithm)

    private fun createRefreshToken(user: CoreUser, expiration: LocalDateTime) =
        createToken(user, expiration, refreshTokenAlgorithm)


    override fun createTokens(user: CoreUser): CoreCredentials {
        val accessTokenValidity = LocalDateTime.now().plusSeconds(validityInS)
        val refreshTokenValidity = LocalDateTime.now().plusSeconds(refreshValidityInS)
        val accessToken = createAccessToken(user, accessTokenValidity)
        val refreshToken = createRefreshToken(user, refreshTokenValidity)

        return CoreCredentials(
            accessToken = accessToken,
            refreshToken = refreshToken,
            accessTokenExpiredTime = accessTokenValidity,
            refreshTokenExpiredTime = refreshTokenValidity
        )
    }

    override fun verifyToken(token: String): String? {
        return accessTokenVerifier.verify(token).claims["id"]?.asString()
    }

    override fun verifyRefreshToken(token: String): String? {
        return refreshTokenVerifier.verify(token).claims["id"]?.asString()
    }

    override fun getTokenVerifier(): JWTVerifier = accessTokenVerifier

}
