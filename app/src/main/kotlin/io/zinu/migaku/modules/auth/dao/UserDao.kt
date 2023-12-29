package io.zinu.migaku.modules.auth.dao

import io.zinu.migaku.exception.UserDoesNotExistsException
import io.zinu.migaku.exception.WrongRequestException
import io.zinu.migaku.modules.auth.model.User
import io.zinu.migaku.modules.auth.model.Users
import io.zinu.migaku.modules.profile.dto.UpdateUserRequest
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.or
import java.util.UUID

interface IUserDao {
    fun createNewUser(email: String, username: String, password: String): User
    fun isExists(email: String?, username: String?): Boolean
    fun getByUserId(userId: String): User?
    fun getAllUsers(): List<User>
    fun updateUser(id: String, updateUser: UpdateUserRequest): User
}

object UserDao : IUserDao {
    override fun createNewUser(email: String, username: String, password: String) = User.new {
        this.username = username
        this.email = email
        this.password = password
    }


    override fun isExists(email: String?, username: String?): Boolean {
        var isExists = false

        //throw error if both are null
        if (email == null && username == null) {
            throw WrongRequestException("email and username cannot be null")
        }
        User.find {
            (if (username != null) {
                Users.username eq username
            } else {
                Op.TRUE
            }) or (if (email != null) {
                Users.email eq email
            } else {
                Op.TRUE
            })
        }.firstOrNull()?.let {
            isExists = true
        }

        return isExists
    }

    override fun getByUserId(userId: String): User? {
        return User.findById(UUID.fromString(userId))
    }

    override fun getAllUsers(): List<User> {
        return User.all().toList()
    }

    override fun updateUser(id: String, updateUser: UpdateUserRequest): User {
        return getByUserId(id)?.apply {
            email = updateUser.user.email ?: email
            password = updateUser.user.password ?: password
            username = updateUser.user.username ?: username
            image = updateUser.user.image ?: image
            bio = updateUser.user.bio ?: bio
        } ?: throw UserDoesNotExistsException()
    }

}