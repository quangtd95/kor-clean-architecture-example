package io.qtd.fungpt.auth.adapter.persist.es.repository

import com.jillesvangurp.ktsearch.indexDocument
import com.jillesvangurp.ktsearch.search
import com.jillesvangurp.searchdsls.querydsl.bool
import com.jillesvangurp.searchdsls.querydsl.matchAll
import com.jillesvangurp.searchdsls.querydsl.term
import io.qtd.fungpt.auth.adapter.persist.es.document.EsUsers
import io.qtd.fungpt.auth.core.model.CoreUser
import io.qtd.fungpt.auth.core.repository.UserPort
import io.qtd.fungpt.common.adapter.database.ElasticsearchProvider
import io.qtd.fungpt.common.adapter.database.parseHitsWithId

class EsUserRepository(private val esProvider: ElasticsearchProvider) : UserPort {
    override suspend fun createNewUser(email: String, password: String): CoreUser {
        val newEsUser = EsUsers(
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
        return result.parseHitsWithId<EsUsers>().isNotEmpty()
    }

    override suspend fun getByUserId(userId: String): CoreUser? {
        val result = esProvider.esClient.search(target = EsUsers.INDEX) {
            query = bool {
                must(
                    term("_id", userId)
                )
            }
        }
        return result.parseHitsWithId<EsUsers>().firstOrNull()?.toCore()
    }

    override suspend fun getByEmail(email: String): CoreUser? {
        val result = esProvider.esClient.search(target = EsUsers.INDEX) {
            query = bool {
                must(
                    term("email.keyword", email)
                )
            }
        }
        return result.parseHitsWithId<EsUsers>().firstOrNull()?.toCore()
    }

    override suspend fun getAllUsers(): List<CoreUser> {
        val result = esProvider.esClient.search(target = EsUsers.INDEX) {
            query = matchAll()
        }
        return result.parseHitsWithId<EsUsers>().map(EsUsers::toCore)
    }
}