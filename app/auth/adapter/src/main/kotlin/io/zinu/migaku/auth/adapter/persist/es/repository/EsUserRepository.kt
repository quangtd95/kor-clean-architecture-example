package io.zinu.migaku.auth.adapter.persist.es.repository

import com.jillesvangurp.ktsearch.ids
import com.jillesvangurp.ktsearch.indexDocument
import com.jillesvangurp.ktsearch.parseHits
import com.jillesvangurp.ktsearch.search
import com.jillesvangurp.searchdsls.querydsl.bool
import com.jillesvangurp.searchdsls.querydsl.term
import io.zinu.migaku.auth.adapter.persist.es.document.EsUsers
import io.zinu.migaku.auth.core.model.CoreUser
import io.zinu.migaku.auth.core.repository.UserPort
import io.zinu.migaku.common.adapter.database.ElasticsearchProvider

class EsUserRepository(private val esProvider: ElasticsearchProvider) : UserPort {
    override suspend fun createNewUser(email: String, password: String): CoreUser {
        val newEsUser = EsUsers(
            id = null,
            email = email,
            password = password,
            bio = "",
            image = "",
        )

        //TODO: validate error
        val indexResponse = esProvider.esClient.indexDocument(
            target = EsUsers.INDEX, document = newEsUser
        )
        newEsUser.id = indexResponse.id

        return newEsUser.toCore()

    }

    override suspend fun isExists(email: String): Boolean {
        val result = esProvider.esClient.search(target = EsUsers.INDEX) {
            query = bool {
                must(
                    term("email.keyword", email)
                )
            }
        }
        return result.parseHits<EsUsers>().isNotEmpty()
    }

    override suspend fun getByUserId(userId: String): CoreUser? {
        val result = esProvider.esClient.search(target = EsUsers.INDEX) {
            query = bool {
                must(
                    term("_id", userId)
                )
            }
        }
        return result.parseHits<EsUsers>().firstOrNull()?.apply {
            this.id = result.ids.first()
        }?.toCore()
    }

    override suspend fun getByEmail(email: String): CoreUser? {
        val result = esProvider.esClient.search(target = EsUsers.INDEX) {
            query = bool {
                must(
                    term("email.keyword", email)
                )
            }
        }
        return result.parseHits<EsUsers>().firstOrNull()?.apply {
            this.id = result.ids.first()
        }?.toCore()
    }

    override suspend fun getAllUsers(): List<CoreUser> {
        val result = esProvider.esClient.search(target = EsUsers.INDEX) {
            query = bool {
                must(
                    term("_id", "*")
                )
            }
        }
        return result.parseHits<EsUsers>()
            .zip(result.ids) { esUser, id ->
                esUser.id = id
                esUser
            }.map(EsUsers::toCore)
    }
}