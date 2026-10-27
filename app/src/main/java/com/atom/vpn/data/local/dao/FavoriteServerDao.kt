package com.atom.vpn.data.local.dao

import androidx.room.*
import com.atom.vpn.data.local.entity.FavoriteServerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteServerDao {
    @Query("SELECT * FROM favorite_servers ORDER BY addedAtMs DESC")
    fun observeAll(): Flow<List<FavoriteServerEntity>>

    @Query("SELECT serverId FROM favorite_servers")
    fun observeIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: FavoriteServerEntity)

    @Query("DELETE FROM favorite_servers WHERE serverId = :serverId")
    suspend fun delete(serverId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_servers WHERE serverId = :serverId)")
    suspend fun exists(serverId: String): Boolean

    @Query("DELETE FROM favorite_servers")
    suspend fun deleteAll()
}
