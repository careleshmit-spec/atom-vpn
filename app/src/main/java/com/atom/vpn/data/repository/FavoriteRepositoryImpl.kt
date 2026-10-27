package com.atom.vpn.data.repository

import com.atom.vpn.data.local.dao.FavoriteServerDao
import com.atom.vpn.data.local.entity.FavoriteServerEntity
import com.atom.vpn.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteRepositoryImpl @Inject constructor(
    private val dao: FavoriteServerDao
) : FavoriteRepository {
    override fun getFavorites(): Flow<List<String>> = dao.observeIds()
    override suspend fun addFavorite(serverId: String) = dao.insert(FavoriteServerEntity(serverId))
    override suspend fun removeFavorite(serverId: String) = dao.delete(serverId)
    override suspend fun isFavorite(serverId: String): Boolean = dao.exists(serverId)
    override suspend fun clearFavorites() = dao.deleteAll()
}
