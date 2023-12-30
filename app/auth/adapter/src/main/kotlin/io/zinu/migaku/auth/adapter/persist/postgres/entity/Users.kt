package io.zinu.migaku.auth.adapter.persist.postgres.entity

import io.zinu.migaku.auth.core.model.CoreUser
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

object Users : UUIDTable() {
    val email = varchar("email", 255).uniqueIndex()
    val bio = text("bio").default("")
    val image = varchar("image", 255).nullable()
    val password = varchar("password", 255)

}

class User(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<User>(Users)

    var email by Users.email
    var bio by Users.bio
    var image by Users.image
    var password by Users.password

    fun toCore(): CoreUser {
        return CoreUser(
            id = id.toString(),
            email = email,
            image = image,
            bio = bio,
            password = password
        )
    }
}


