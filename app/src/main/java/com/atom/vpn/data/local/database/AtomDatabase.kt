package com.atom.vpn.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.atom.vpn.data.local.dao.FavoriteServerDao
import com.atom.vpn.data.local.entity.FavoriteServerEntity

@Database(
    entities = [FavoriteServerEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AtomDatabase : RoomDatabase() {
    abstract fun favoriteServerDao(): FavoriteServerDao
}
