package io.qtd.fungpt.auth.adapter.persist.postgres.entities

import io.qtd.fungpt.auth.core.models.CoreUser
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

object PgUsers : UUIDTable("users") {
    val email = varchar("email", 255).uniqueIndex()
    val password = varchar("password", 255)

}

class PgUser(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<PgUser>(PgUsers)

    var email by PgUsers.email
    var password by PgUsers.password

    fun toCore() = CoreUser(
        id = id.toString(),
        email = email,
        password = password
    )
}


