package io.qtd.fungpt.profile.adapter.persist.es.repositories

import com.jillesvangurp.ktsearch.deleteByQuery
import com.jillesvangurp.ktsearch.indexDocument
import com.jillesvangurp.ktsearch.parseHits
import com.jillesvangurp.ktsearch.search
import com.jillesvangurp.searchdsls.querydsl.matchAll
import com.jillesvangurp.searchdsls.querydsl.term
import io.qtd.fungpt.common.adapter.databases.ElasticsearchProvider
import io.qtd.fungpt.profile.adapter.persist.es.documents.EsProfiles
import io.qtd.fungpt.profile.core.models.CoreProfile
import io.qtd.fungpt.profile.core.ports.ProfilePort
import kotlinx.datetime.toKotlinLocalDateTime

class EsProfileRepository(private val esProvider: ElasticsearchProvider) : ProfilePort {
    companion object {
        const val PROFILE_INDEX = EsProfiles.INDEX
    }

    override suspend fun getByProfileId(profileId: String): CoreProfile? {
        val result = esProvider.esClient.search(target = PROFILE_INDEX) {
            query = term(EsProfiles::id, profileId)
        }
        return result.parseHits<EsProfiles>().firstOrNull()?.toCore()
    }


    override suspend fun getAllProfiles(): List<CoreProfile> {
        val result = esProvider.esClient.search(target = PROFILE_INDEX) {
            query = matchAll()
        }
        return result.parseHits<EsProfiles>().map(EsProfiles::toCore)
    }

    override suspend fun createProfile(profile: CoreProfile): CoreProfile {
        esProvider.esClient.indexDocument(
            target = PROFILE_INDEX,
            document = EsProfiles(
                id = profile.id,
                email = profile.email,
                bio = profile.bio,
                avatar = profile.avatar,
                createdAt = profile.createdAt.toKotlinLocalDateTime()
            )
        )
        return profile
    }

    override suspend fun deleteProfile(profileId: String) {
        esProvider.esClient.deleteByQuery(
            target = PROFILE_INDEX,
        ) {
            query = term(EsProfiles::id, profileId)
        }
    }
}