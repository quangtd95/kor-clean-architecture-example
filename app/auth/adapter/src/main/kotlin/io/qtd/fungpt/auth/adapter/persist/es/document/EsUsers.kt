package io.qtd.fungpt.auth.adapter.persist.es.document

import com.jillesvangurp.searchdsls.mappingdsl.IndexSettingsAndMappingsDSL
import io.qtd.fungpt.auth.core.model.CoreUser
import io.qtd.fungpt.common.adapter.base.EsBaseIdDocument
import kotlinx.serialization.Serializable

@Serializable
data class EsUsers(
    val id: String,
    val email: String,
    val password: String
) : EsBaseIdDocument() {
    companion object {
        const val INDEX = "auth_users"

        val MAPPING = IndexSettingsAndMappingsDSL().apply {
            mappings(dynamicEnabled = true) {
                keyword(EsUsers::id)
                keyword(EsUsers::email)
                keyword(EsUsers::password)
            }
        }

    }

    fun toCore(): CoreUser {
        return CoreUser(
            id = id, email = email, password = password
        )
    }
}



