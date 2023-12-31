package io.zinu.migaku.auth.adapter.persist.postgres.repository

import io.zinu.migaku.auth.adapter.persist.postgres.entity.User
import io.zinu.migaku.auth.adapter.persist.postgres.entity.Users
import io.zinu.migaku.auth.core.model.CoreUser
import io.zinu.migaku.auth.core.repository.UserPort
import org.jetbrains.exposed.sql.SchemaUtils
import java.util.*

object UserRepository : UserPort {

    override fun createNewUser(email: String, password: String) = User.new {
        this.email = email
        this.password = password
    }.toCore()

    override fun isExists(email: String) = User.find { Users.email eq email }.firstOrNull() != null

    override fun getByUserId(userId: String) = User.findById(UUID.fromString(userId))?.toCore()

    override fun getByEmail(email: String): CoreUser? = User.find { (Users.email eq email) }.firstOrNull()?.toCore()

    override fun getAllUsers() = User.all().map(User::toCore)
}