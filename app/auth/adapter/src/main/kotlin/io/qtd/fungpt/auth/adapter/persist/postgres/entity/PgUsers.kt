package io.qtd.fungpt.auth.adapter.persist.postgres.entity

import io.qtd.fungpt.auth.core.model.CoreUser
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

object PgUsers : UUIDTable("users") {
    val email = varchar("email", 255).uniqueIndex()
    val bio = text("bio").default("")
    val image = varchar("image", 255).nullable()
    val password = varchar("password", 255)

}

class PgUser(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<PgUser>(PgUsers)

    var email by PgUsers.email
    var bio by PgUsers.bio
    var image by PgUsers.image
    var password by PgUsers.password

    fun toCore() = CoreUser(
        id = id.toString(),
        email = email,
        image = image,
        bio = bio,
        password = password
    )
}


