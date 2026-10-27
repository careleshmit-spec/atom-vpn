package com.atom.vpn.di

import com.atom.vpn.data.datastore.AtomPreferences
import com.atom.vpn.data.repository.FavoriteRepositoryImpl
import com.atom.vpn.data.repository.VpnServerRepositoryImpl
import com.atom.vpn.domain.model.AppSettings
import com.atom.vpn.domain.repository.FavoriteRepository
import com.atom.vpn.domain.repository.SettingsRepository
import com.atom.vpn.domain.repository.VpnServerRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindVpnServerRepository(impl: VpnServerRepositoryImpl): VpnServerRepository

    @Binds @Singleton
    abstract fun bindFavoriteRepository(impl: FavoriteRepositoryImpl): FavoriteRepository

    companion object {
        @Provides @Singleton
        fun provideSettingsRepository(prefs: AtomPreferences): SettingsRepository =
            object : SettingsRepository {
                override fun getSettings(): Flow<AppSettings> = prefs.settings
                override suspend fun updateSettings(settings: AppSettings) = prefs.updateSettings(settings)
            }
    }
}
