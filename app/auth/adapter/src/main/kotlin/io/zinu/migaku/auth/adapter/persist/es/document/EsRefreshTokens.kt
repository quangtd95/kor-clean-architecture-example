package io.zinu.migaku.auth.adapter.persist.es.document

import com.jillesvangurp.searchdsls.mappingdsl.IndexSettingsAndMappingsDSL
import io.zinu.migaku.auth.core.model.CoreRefreshToken
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class EsRefreshTokens(
    var id: String?,
    val token: String,
    val userId: String,
    val expiresAt: LocalDateTime,
    val createdAt: LocalDateTime,
    val revoked: Boolean,
) {


    companion object {
        const val INDEX = "refresh_tokens"

        val MAPPING = IndexSettingsAndMappingsDSL().apply {
            mappings(dynamicEnabled = true) {
                text(EsRefreshTokens::token) {
                    fields {
                        keyword("keyword") {
                            ignoreAbove = "1024"
                        }
                    }
                }
                text(EsRefreshTokens::userId) {
                    fields {
                        keyword("keyword")
                    }
                }
                date(EsRefreshTokens::expiresAt)
                date(EsRefreshTokens::createdAt)
                bool(EsRefreshTokens::revoked) {
                    fields {
                        keyword("keyword")
                    }
                }

            }
        }
    }

    fun toCore() = CoreRefreshToken(
        token = token,
        userId = userId,
        expiresAt = expiresAt.toJavaLocalDateTime(),
        createdAt = createdAt.toJavaLocalDateTime(),
        revoked = revoked
    )
}