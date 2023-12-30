package io.zinu.migaku.auth.service

import io.zinu.migaku.auth.repository.IRefreshTokenRepository
import io.zinu.migaku.auth.repository.IUserRepository
import io.zinu.migaku.auth.dto.RegisterUserRequest
import io.zinu.migaku.auth.dto.UserCredentialsResponse
import io.zinu.migaku.auth.model.User
import io.zinu.migaku.auth.model.Users
import io.zinu.migaku.common.base.BaseService
import io.zinu.migaku.common.exception.LoginCredentialsInvalidException
import io.zinu.migaku.common.exception.RefreshTokenInvalidException
import io.zinu.migaku.common.exception.UserDoesNotExistsException
import io.zinu.migaku.common.exception.UserExistsException
import io.zinu.migaku.common.utils.unless
import org.koin.core.component.inject

interface IAuthService {
    suspend fun register(registerUser: RegisterUserRequest): UserCredentialsResponse
    suspend fun login(email: String, password: String): UserCredentialsResponse
    suspend fun refresh(refreshToken: String): UserCredentialsResponse
    suspend fun logout(userId: String)
}

class AuthService : BaseService(), IAuthService {
    private val passwordService by inject<IPasswordService>()
    private val tokenService by inject<ITokenService>()
    private val refreshTokenDao by inject<IRefreshTokenRepository>()
    private val userDao by inject<IUserRepository>()

    override suspend fun register(registerUser: RegisterUserRequest): UserCredentialsResponse = dbQuery {
        if (userDao.isExists(registerUser.user.email, registerUser.user.username)) {
            throw UserExistsException(
                mapOf(
                    "email" to registerUser.user.email, "username" to registerUser.user.username
                )
            )
        }

        passwordService.validateFeasiblePassword(registerUser.user.password)

        val newUser = userDao.createNewUser(
            email = registerUser.user.email,
            username = registerUser.user.username,
            password = passwordService.encryptPassword(registerUser.user.password)
        )
        val tokens = tokenService.createTokens(newUser)
        refreshTokenDao.newRefreshToken(newUser.id.toString(), tokens.refreshToken, tokens.refreshTokenExpiredTime)

        UserCredentialsResponse.fromUser(newUser, tokens)
    }

    override suspend fun login(email: String, password: String): UserCredentialsResponse = dbQuery {
        val user = User.find { (Users.email eq email) }.firstOrNull()
            ?: throw UserDoesNotExistsException(mapOf("email" to email))

        if (passwordService.validatePassword(password, user.password)) {

            refreshTokenDao.revokeAllTokens(user.id.toString())

            val tokens = tokenService.createTokens(user)
            refreshTokenDao.newRefreshToken(user.id.toString(), tokens.refreshToken, tokens.refreshTokenExpiredTime)

            UserCredentialsResponse.fromUser(user, tokens)
        } else {
            throw LoginCredentialsInvalidException()
        }
    }

    override suspend fun refresh(refreshToken: String): UserCredentialsResponse {
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
            dbQuery {
                unless(refreshTokenDao.verifyToken(refreshToken)) {
                    throw RefreshTokenInvalidException()
                }
            }
            val user = getUserById(userId)
            val tokens = tokenService.createTokens(user)
            dbQuery {
                refreshTokenDao.deleteToken(refreshToken)
                refreshTokenDao.newRefreshToken(
                    user.id.toString(),
                    tokens.refreshToken,
                    tokens.refreshTokenExpiredTime,
                )
            }
            return UserCredentialsResponse.fromUser(user, tokens)
        }

        throw RefreshTokenInvalidException()

    }


    private suspend fun getUserById(id: String) = dbQuery {
        userDao.getByUserId(id) ?: throw UserDoesNotExistsException()
    }

    override suspend fun logout(userId: String) {
        dbQuery {
            refreshTokenDao.revokeAllTokens(userId)
        }
    }
}
