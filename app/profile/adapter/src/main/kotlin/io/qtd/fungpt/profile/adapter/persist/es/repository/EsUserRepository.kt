package io.qtd.fungpt.profile.adapter.persist.es.repository

import com.jillesvangurp.ktsearch.parseHits
import com.jillesvangurp.ktsearch.search
import com.jillesvangurp.searchdsls.querydsl.matchAll
import com.jillesvangurp.searchdsls.querydsl.term
import io.qtd.fungpt.common.adapter.database.ElasticsearchProvider
import io.qtd.fungpt.profile.adapter.persist.es.document.EsProfiles
import io.qtd.fungpt.profile.core.model.CoreProfile
import io.qtd.fungpt.profile.core.repository.ProfilePort

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
}