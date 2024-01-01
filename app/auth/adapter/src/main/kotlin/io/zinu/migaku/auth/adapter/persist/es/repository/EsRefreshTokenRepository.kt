package io.zinu.migaku.auth.adapter.persist.es.repository

import com.jillesvangurp.ktsearch.deleteDocument
import com.jillesvangurp.ktsearch.indexDocument
import com.jillesvangurp.ktsearch.parseHits
import com.jillesvangurp.ktsearch.search
import com.jillesvangurp.searchdsls.querydsl.bool
import com.jillesvangurp.searchdsls.querydsl.term
import io.zinu.migaku.auth.adapter.persist.es.document.EsRefreshTokens
import io.zinu.migaku.auth.core.model.CoreRefreshToken
import io.zinu.migaku.auth.core.repository.RefreshTokenPort
import io.zinu.migaku.common.adapter.database.ElasticsearchProvider
import io.zinu.migaku.common.core.extension.randomUUID
import kotlinx.datetime.toKotlinLocalDateTime
import java.time.LocalDateTime

class EsRefreshTokenRepository(private val esProvider: ElasticsearchProvider) :
    RefreshTokenPort {

    override suspend fun newRefreshToken(userId: String, token: String, expiredTime: LocalDateTime): CoreRefreshToken {
        val newEsRefreshToken = EsRefreshTokens(
            id = randomUUID(),
            token = token,
            userId = userId,
            expiresAt = expiredTime.toKotlinLocalDateTime(),
            createdAt = LocalDateTime.now().toKotlinLocalDateTime(),
            revoked = false,
        )

        //TODO: validate error
        esProvider.esClient.indexDocument(
            target = EsRefreshTokens.INDEX, document = newEsRefreshToken
        )

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
        return result.parseHits<EsRefreshTokens>().isNotEmpty()
    }

    override suspend fun revokeAllTokens(userId: String) {
        val result = esProvider.esClient.search(target = EsRefreshTokens.INDEX) {
            query = bool {
                must(
                    term("id.keyword", userId),
                    term("revoked.keyword", "false"),
                )
            }
        }
        result.parseHits<EsRefreshTokens>().forEach {
            esProvider.esClient.indexDocument(
                target = EsRefreshTokens.INDEX, document = it.copy(revoked = true)
            )
        }
    }

    override suspend fun deleteToken(token: String) {
        val result = esProvider.esClient.search(target = EsRefreshTokens.INDEX) {
            query = bool {
                must(
                    term("revoked.keyword", token),
                )
            }
        }
        result.parseHits<EsRefreshTokens>().forEach {
            esProvider.esClient.deleteDocument(
                target = EsRefreshTokens.INDEX, id = it.id
            )
        }
    }
}