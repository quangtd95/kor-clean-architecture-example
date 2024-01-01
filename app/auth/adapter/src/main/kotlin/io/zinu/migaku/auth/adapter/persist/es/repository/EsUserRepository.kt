package io.zinu.migaku.auth.adapter.persist.es.repository

import com.jillesvangurp.ktsearch.indexDocument
import com.jillesvangurp.ktsearch.parseHits
import com.jillesvangurp.ktsearch.search
import com.jillesvangurp.searchdsls.querydsl.bool
import com.jillesvangurp.searchdsls.querydsl.term
import io.zinu.migaku.auth.adapter.persist.es.document.EsUsers
import io.zinu.migaku.auth.core.model.CoreUser
import io.zinu.migaku.auth.core.repository.UserPort
import io.zinu.migaku.common.adapter.database.ElasticsearchProvider
import io.zinu.migaku.common.core.extension.randomUUID

class EsUserRepository(private val esProvider: ElasticsearchProvider) : UserPort {
    override suspend fun createNewUser(email: String, password: String): CoreUser {
        val newEsUser = EsUsers(
            id = randomUUID(),
            email = email,
            password = password,
            bio = "",
            image = "",
        )

        //TODO: validate error
        esProvider.esClient.indexDocument(
            target = EsUsers.INDEX, document = newEsUser
        )

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
                    term("id.keyword", userId)
                )
            }
        }
        return result.parseHits<EsUsers>().firstOrNull()?.toCore()
    }

    override suspend fun getByEmail(email: String): CoreUser? {
        val result = esProvider.esClient.search(target = EsUsers.INDEX) {
            query = bool {
                must(
                    term("email.keyword", email)
                )
            }
        }
        return result.parseHits<EsUsers>().firstOrNull()?.toCore()
    }

    override suspend fun getAllUsers(): List<CoreUser> {
        val result = esProvider.esClient.search(target = EsUsers.INDEX) {
            query = bool {
                must(
                    term("id.keyword", "*")
                )
            }
        }
        return result.parseHits<EsUsers>().map(EsUsers::toCore)
    }
}