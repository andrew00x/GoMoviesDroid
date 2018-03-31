package com.andrew00x.gomoviesdroid

import com.andrew00x.gomoviesdroid.config.AAConfigurationService
import com.andrew00x.gomoviesdroid.config.ConfigurationService
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class GomoviesModule {
    @Singleton
    @Provides
    fun gomoviesApi(configService: ConfigurationService): GomoviesApi {
        val serverUrl = configService.retrieve().serverUrl
        val retrofit = Retrofit.Builder()
                .baseUrl(if (serverUrl.isNotEmpty()) serverUrl else "http://localhost:8000/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        return retrofit.create(GomoviesApi::class.java)
    }

    @Singleton
    @Provides
    fun configurationService(): ConfigurationService {
        return AAConfigurationService()
    }

    @Singleton
    @Provides
    fun apiService(api: GomoviesApi, configService: ConfigurationService): GomoviesService {
        return DefaultGomoviesService(api, configService)
    }
}
