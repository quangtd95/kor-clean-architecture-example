package io.qtd.fungpt.auth.adapter.persist.es.repositories

import com.jillesvangurp.ktsearch.indexDocument
import com.jillesvangurp.ktsearch.parseHits
import com.jillesvangurp.ktsearch.search
import com.jillesvangurp.searchdsls.querydsl.matchAll
import com.jillesvangurp.searchdsls.querydsl.term
import io.qtd.fungpt.auth.adapter.persist.es.documents.EsUsers
import io.qtd.fungpt.auth.core.models.CoreUser
import io.qtd.fungpt.auth.core.ports.UserPort
import io.qtd.fungpt.common.adapter.databases.ElasticsearchProvider
import io.qtd.fungpt.common.core.extension.randomUUID
import kotlinx.datetime.toKotlinLocalDateTime
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

class EsUserRepository(private val esProvider: ElasticsearchProvider) : UserPort {
    private val logger = LoggerFactory.getLogger(EsUserRepository::class.java)
    override suspend fun createNewUser(email: String, password: String): CoreUser {
        val newEsUser = EsUsers(
            id = randomUUID(),
            email = email,
            password = password,
            createdAt = LocalDateTime.now().toKotlinLocalDateTime()
        )

        val indexResponse = esProvider.esClient.indexDocument(
            target = EsUsers.INDEX, document = newEsUser
        )
        logger.info("Create new user with id: ${indexResponse.id}")
        return newEsUser.toCore()

    }

    override suspend fun isExists(email: String): Boolean {
        val result = esProvider.esClient.search(target = EsUsers.INDEX) {
            query = term(EsUsers::email, email)
        }
        return result.parseHits<EsUsers>().isNotEmpty()
    }

    override suspend fun getByUserId(userId: String): CoreUser? {
        val result = esProvider.esClient.search(target = EsUsers.INDEX) {
            query = term(EsUsers::id, userId)
        }
        return result.parseHits<EsUsers>().firstOrNull()?.toCore()
    }

    override suspend fun getByEmail(email: String): CoreUser? {
        val result = esProvider.esClient.search(target = EsUsers.INDEX) {
            query = term(EsUsers::email, email)
        }
        return result.parseHits<EsUsers>().firstOrNull()?.toCore()
    }

    override suspend fun getAllUsers(): List<CoreUser> {
        val result = esProvider.esClient.search(target = EsUsers.INDEX) {
            query = matchAll()
        }
        return result.parseHits<EsUsers>().map(EsUsers::toCore)
    }
}