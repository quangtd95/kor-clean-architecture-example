package io.qtd.fungpt.profile.adapter.persist.es.documents

import com.jillesvangurp.searchdsls.mappingdsl.IndexSettingsAndMappingsDSL
import io.qtd.fungpt.common.adapter.bases.EsBaseIdDocument
import io.qtd.fungpt.profile.core.models.CoreProfile
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class EsProfiles(
    val id: String,
    val email: String,
    val bio: String?,
    val avatar: String?,
    val createdAt: LocalDateTime,
) : EsBaseIdDocument() {
    companion object {
        const val INDEX = "profiles"

        val MAPPING = IndexSettingsAndMappingsDSL().apply {
            mappings(dynamicEnabled = true) {
                keyword(EsProfiles::id)
                keyword(EsProfiles::email)
                text(EsProfiles::bio)
                text(EsProfiles::avatar)
                date(EsProfiles::createdAt)
            }
        }

    }

    fun toCore(): CoreProfile {
        return CoreProfile(
            id = id,
            email = email,
            bio = bio,
            avatar = avatar,
            createdAt = createdAt.toJavaLocalDateTime()
        )
    }
}



