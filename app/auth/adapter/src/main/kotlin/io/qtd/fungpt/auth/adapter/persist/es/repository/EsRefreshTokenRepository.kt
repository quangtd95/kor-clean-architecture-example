package io.qtd.fungpt.auth.adapter.persist.es.repository

import com.jillesvangurp.ktsearch.*
import com.jillesvangurp.searchdsls.querydsl.bool
import com.jillesvangurp.searchdsls.querydsl.term
import io.qtd.fungpt.auth.adapter.persist.es.document.EsRefreshTokens
import io.qtd.fungpt.auth.core.model.CoreRefreshToken
import io.qtd.fungpt.auth.core.repository.RefreshTokenPort
import io.qtd.fungpt.common.adapter.database.ElasticsearchProvider
import io.qtd.fungpt.common.adapter.database.parseHitsWithId
import kotlinx.datetime.toKotlinLocalDateTime
import java.time.LocalDateTime

class EsRefreshTokenRepository(private val esProvider: ElasticsearchProvider) : RefreshTokenPort {

    override suspend fun newRefreshToken(userId: String, token: String, expiredTime: LocalDateTime): CoreRefreshToken {
        val newEsRefreshToken = EsRefreshTokens(
            token = token,
            userId = userId,
            expiresAt = expiredTime.toKotlinLocalDateTime(),
            createdAt = LocalDateTime.now().toKotlinLocalDateTime(),
            revoked = false,
        )

        val indexResponse = esProvider.esClient.indexDocument(
            target = EsRefreshTokens.INDEX, document = newEsRefreshToken
        )
        newEsRefreshToken.id = indexResponse.id

        return newEsRefreshToken.toCore()
    }

    override suspend fun verifyToken(token: String): Boolean {
        val result = esProvider.esClient.search(target = EsRefreshTokens.INDEX) {
            query = bool {
                must(
                    term("token.keyword", token),
                    term("revoked.keyword", "false"),
                )
            }
        }
        return result.parseHitsWithId<EsRefreshTokens>().isNotEmpty()
    }

    override suspend fun revokeAllTokens(userId: String) {
        val result = esProvider.esClient.search(target = EsRefreshTokens.INDEX) {
            query = bool {
                must(
                    term("userId.keyword", userId),
                    term("revoked.keyword", "false"),
                )
            }
        }

        result
            .parseHitsWithId<EsRefreshTokens>()
            .forEach {
                esProvider.esClient.deleteDocument(
                    target = EsRefreshTokens.INDEX,
                    id = it.id!!
                )
            }
    }

    override suspend fun deleteToken(token: String) {
        val result = esProvider.esClient.search(target = EsRefreshTokens.INDEX) {
            query = bool {
                must(
                    term("token.keyword", token),
                    term("revoked.keyword", "false"),
                )
            }
        }
        result.parseHitsWithId<EsRefreshTokens>()
            .forEach {
                esProvider.esClient.deleteDocument(
                    target = EsRefreshTokens.INDEX, id = it.id!!
                )
            }
    }

}