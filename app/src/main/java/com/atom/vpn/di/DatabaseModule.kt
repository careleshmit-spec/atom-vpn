package com.atom.vpn.di

import android.content.Context
import androidx.room.Room
import com.atom.vpn.data.local.dao.FavoriteServerDao
import com.atom.vpn.data.local.database.AtomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAtomDatabase(@ApplicationContext ctx: Context): AtomDatabase =
        Room.databaseBuilder(ctx, AtomDatabase::class.java, "atom_db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideFavoriteServerDao(db: AtomDatabase): FavoriteServerDao = db.favoriteServerDao()
}
