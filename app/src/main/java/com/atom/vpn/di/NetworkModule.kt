package com.atom.vpn.di

import com.atom.vpn.data.remote.api.VpnGateApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()

    @Provides
    @Singleton
    fun provideVpnGateApi(client: OkHttpClient): VpnGateApi = Retrofit.Builder()
        .baseUrl("https://www.vpngate.net/")
        .client(client)
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()
        .create(VpnGateApi::class.java)
}
