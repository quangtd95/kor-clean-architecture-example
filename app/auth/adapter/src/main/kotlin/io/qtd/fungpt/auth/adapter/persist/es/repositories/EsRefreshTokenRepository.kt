package io.qtd.fungpt.auth.adapter.persist.es.repositories

import com.jillesvangurp.ktsearch.*
import com.jillesvangurp.searchdsls.querydsl.bool
import com.jillesvangurp.searchdsls.querydsl.term
import io.qtd.fungpt.auth.adapter.persist.es.documents.EsRefreshTokens
import io.qtd.fungpt.auth.core.models.CoreRefreshToken
import io.qtd.fungpt.auth.core.repositories.RefreshTokenPort
import io.qtd.fungpt.common.adapter.databases.ElasticsearchProvider
import io.qtd.fungpt.common.core.extension.randomUUID
import kotlinx.datetime.toKotlinLocalDateTime
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

class EsRefreshTokenRepository(private val esProvider: ElasticsearchProvider) : RefreshTokenPort {
    private val logger = LoggerFactory.getLogger(EsRefreshTokenRepository::class.java)
    override suspend fun newRefreshToken(userId: String, token: String, expiredTime: LocalDateTime): CoreRefreshToken {
        val newEsRefreshToken = EsRefreshTokens(
            id = randomUUID(),
            token = token,
            userId = userId,
            expiresAt = expiredTime.toKotlinLocalDateTime(),
            createdAt = LocalDateTime.now().toKotlinLocalDateTime(),
            revoked = false,
        )

        val indexResponse = esProvider.esClient.indexDocument(
            target = EsRefreshTokens.INDEX, document = newEsRefreshToken
        )
        logger.info("Create new refresh token with id: ${indexResponse.id}")

        return newEsRefreshToken.toCore()
    }

    override suspend fun verifyToken(token: String): Boolean {
        val result = esProvider.esClient.search(target = EsRefreshTokens.INDEX) {
            query = bool {
                must(
                    term(EsRefreshTokens::token, token),
                    term(EsRefreshTokens::revoked, false.toString()),
                )
            }
        }
        return result.parseHits<EsRefreshTokens>().isNotEmpty()
    }

    override suspend fun revokeAllTokens(userId: String) {
        esProvider.esClient.deleteByQuery(target = EsRefreshTokens.INDEX) {
            query = term(EsRefreshTokens::userId, userId)
        }
    }

    override suspend fun deleteToken(token: String) {
        esProvider.esClient.deleteByQuery(target = EsRefreshTokens.INDEX) {
            query = term(EsRefreshTokens::token, token)
        }
    }

}