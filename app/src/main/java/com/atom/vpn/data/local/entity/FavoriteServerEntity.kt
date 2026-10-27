package com.atom.vpn.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_servers")
data class FavoriteServerEntity(
    @PrimaryKey val serverId: String,
    val addedAtMs: Long = System.currentTimeMillis()
)
