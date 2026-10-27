package com.atom.vpn.domain.repository

import com.atom.vpn.domain.model.VpnServer
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun getFavorites(): Flow<List<String>>          // emits server IDs
    suspend fun addFavorite(serverId: String)
    suspend fun removeFavorite(serverId: String)
    suspend fun isFavorite(serverId: String): Boolean
    suspend fun clearFavorites()
}
