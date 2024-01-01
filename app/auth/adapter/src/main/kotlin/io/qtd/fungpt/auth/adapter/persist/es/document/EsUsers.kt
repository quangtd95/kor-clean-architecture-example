package io.qtd.fungpt.auth.adapter.persist.es.document

import com.jillesvangurp.searchdsls.mappingdsl.IndexSettingsAndMappingsDSL
import io.qtd.fungpt.auth.core.model.CoreUser
import io.qtd.fungpt.common.adapter.base.EsBaseIdDocument
import kotlinx.serialization.Serializable

@Serializable
data class EsUsers(
    val email: String,
    val bio: String,
    val image: String,
    val password: String
) : EsBaseIdDocument() {
    companion object {
        const val INDEX = "users"

        val MAPPING = IndexSettingsAndMappingsDSL().apply {
            mappings(dynamicEnabled = false) {
                text(EsUsers::email) {
                    fields {
                        keyword("keyword")
                    }
                }
                text(EsUsers::bio)
                text(EsUsers::image)
                text(EsUsers::password)
            }
        }

    }

    fun toCore(): CoreUser {
        return CoreUser(
            id = id!!, email = email, image = image, bio = bio, password = password
        )
    }
}



