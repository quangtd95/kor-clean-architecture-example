package io.zinu.migaku.auth.adapter.persist.es.document

import io.zinu.migaku.auth.core.model.CoreUser
import kotlinx.serialization.Serializable

@Serializable
//TODO missing mapping for this document when creating the index
data class EsUsers(
    val id: String,
    val email: String,
    val bio: String,
    val image: String,
    val password: String
) {
    companion object {
        const val INDEX = "users"
    }

    fun toCore(): CoreUser {
        return CoreUser(
            id = id,
            email = email,
            image = image,
            bio = bio,
            password = password
        )
    }
}

