package io.zinu.migaku.auth.core.service

import io.zinu.migaku.auth.core.model.CoreUserCredential
import io.zinu.migaku.auth.core.repository.RefreshTokenPort
import io.zinu.migaku.auth.core.repository.UserPort
import io.zinu.migaku.common.database.PersistTransactionPort
import io.zinu.migaku.auth.core.usecase.AuthUsecase
import io.zinu.migaku.auth.core.usecase.TokenUsecase
import io.zinu.migaku.common.exception.LoginCredentialsInvalidException
import io.zinu.migaku.common.exception.RefreshTokenInvalidException
import io.zinu.migaku.common.exception.UserDoesNotExistsException
import io.zinu.migaku.common.exception.UserExistsException
import io.zinu.migaku.common.utils.unless

class AuthService(
    private val passwordService: IPasswordService,
    private val tokenService: TokenUsecase,
    private val refreshTokenPort: RefreshTokenPort,
    private val userPort: UserPort,
    private val txPort: PersistTransactionPort,
) : AuthUsecase {

    override suspend fun register(email: String, password: String): CoreUserCredential = txPort.withNewTransaction {
        if (userPort.isExists(email)) {
            throw UserExistsException(mapOf("email" to email))
        }

        passwordService.validateFeasiblePassword(password)

        val newUser = userPort.createNewUser(
            email = email,
            password = passwordService.encryptPassword(password)
        )
        val tokens = tokenService.createTokens(newUser)
        tokens.refreshToken.let {
            refreshTokenPort.newRefreshToken(
                newUser.id,
                it, tokens.refreshTokenExpiredTime
            )
        }

        CoreUserCredential(newUser, tokens)
    }

    override suspend fun login(email: String, password: String): CoreUserCredential = txPort.withNewTransaction {
        val user = userPort.getByEmail(email) ?: throw UserDoesNotExistsException(mapOf("email" to email))

        if (passwordService.validatePassword(password, user.password)) {

            refreshTokenPort.revokeAllTokens(user.id)

            val tokens = tokenService.createTokens(user)
            refreshTokenPort.newRefreshToken(user.id, tokens.refreshToken, tokens.refreshTokenExpiredTime)

            CoreUserCredential(user, tokens)
        } else {
            throw LoginCredentialsInvalidException()
        }
    }

    override suspend fun refresh(refreshToken: String): CoreUserCredential {
        /**
         * validate refresh token by verifier whether it is expired or not
         * if it is expired, throw AuthenticationException
         * if it is not expired, get the refresh token from the database
         * if it is not found, throw AuthenticationException
         * if it is found, check if the refresh token is revoked or not
         * if it is revoked, throw AuthenticationException
         * if it is not revoked, get the user from the database
         * generate new refreshToken and accessToken
         * revoke all old refreshTokens from the database
         * save the refreshToken to the database
         * return the new refreshToken and accessToken
         */
        tokenService.verifyRefreshToken(refreshToken)?.let { userId ->
            txPort.withNewTransaction {
                unless(refreshTokenPort.verifyToken(refreshToken)) {
                    throw RefreshTokenInvalidException()
                }
            }
            val user = getUserById(userId)
            val tokens = tokenService.createTokens(user)
            txPort.withNewTransaction {
                refreshTokenPort.deleteToken(refreshToken)
                refreshTokenPort.newRefreshToken(
                    user.id,
                    tokens.refreshToken,
                    tokens.refreshTokenExpiredTime,
                )
            }
            return CoreUserCredential(user, tokens)
        }

        throw RefreshTokenInvalidException()

    }


    private suspend fun getUserById(id: String) = txPort.withNewTransaction {
        userPort.getByUserId(id) ?: throw UserDoesNotExistsException()
    }

    override suspend fun logout(userId: String) {
        txPort.withNewTransaction {
            refreshTokenPort.revokeAllTokens(userId)
        }
    }
}
