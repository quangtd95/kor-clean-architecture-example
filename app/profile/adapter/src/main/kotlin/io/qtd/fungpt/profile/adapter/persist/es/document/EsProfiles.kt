package io.qtd.fungpt.profile.adapter.persist.es.document

import com.jillesvangurp.searchdsls.mappingdsl.IndexSettingsAndMappingsDSL
import io.qtd.fungpt.common.adapter.base.EsBaseIdDocument
import io.qtd.fungpt.profile.core.model.CoreProfile
import kotlinx.serialization.Serializable

@Serializable
data class EsProfiles(
    val id: String,
    val email: String,
    val bio: String,
    val avatar: String,
) : EsBaseIdDocument() {
    companion object {
        const val INDEX = "profiles"

        val MAPPING = IndexSettingsAndMappingsDSL().apply {
            mappings(dynamicEnabled = true) {
                keyword(EsProfiles::id)
                keyword(EsProfiles::email)
                text(EsProfiles::bio)
                text(EsProfiles::avatar)
            }
        }

    }

    fun toCore(): CoreProfile {
        return CoreProfile(
            id = id, email = email, bio = bio, avatar = avatar
        )
    }
}



