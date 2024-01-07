package io.qtd.fungpt.auth.adapter.persist.es.documents

import com.jillesvangurp.searchdsls.mappingdsl.IndexSettingsAndMappingsDSL
import io.qtd.fungpt.auth.core.models.CoreUser
import io.qtd.fungpt.common.adapter.bases.EsBaseIdDocument
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class EsUsers(
    val id: String,
    val email: String,
    val password: String,
    val createdAt: LocalDateTime,
) : EsBaseIdDocument() {
    companion object {
        const val INDEX = "auth_users"

        val MAPPING = IndexSettingsAndMappingsDSL().apply {
            mappings(dynamicEnabled = true) {
                keyword(EsUsers::id)
                keyword(EsUsers::email)
                keyword(EsUsers::password)
                date(EsUsers::createdAt)
            }
        }

    }

    fun toCore(): CoreUser {
        return CoreUser(
            id = id, email = email, password = password
        )
    }
}



