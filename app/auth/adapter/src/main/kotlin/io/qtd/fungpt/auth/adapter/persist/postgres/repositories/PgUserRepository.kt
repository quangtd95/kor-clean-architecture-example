package io.qtd.fungpt.auth.adapter.persist.postgres.repositories

import io.qtd.fungpt.auth.adapter.persist.postgres.entities.PgUser
import io.qtd.fungpt.auth.adapter.persist.postgres.entities.PgUsers
import io.qtd.fungpt.auth.core.models.CoreUser
import io.qtd.fungpt.auth.core.repositories.UserPort
import java.util.*

object PgUserRepository : UserPort {

    override suspend fun createNewUser(email: String, password: String) = PgUser.new {
        this.email = email
        this.password = password
    }.toCore()

    override suspend fun isExists(email: String) = PgUser.find { PgUsers.email eq email }.firstOrNull() != null

    override suspend fun getByUserId(userId: String) = PgUser.findById(UUID.fromString(userId))?.toCore()

    override suspend fun getByEmail(email: String): CoreUser? = PgUser.find { (PgUsers.email eq email) }.firstOrNull()?.toCore()

    override suspend fun getAllUsers() = PgUser.all().map(PgUser::toCore)
}