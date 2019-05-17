package com.andrew00x.gomoviesdroid

import android.content.Context
import com.andrew00x.gomoviesdroid.config.ConfigurationRepository
import com.andrew00x.gomoviesdroid.config.SharedPreferencesConfigurationRepository
import com.andrew00x.gomoviesdroid.player.AAPlaybackRepository
import com.andrew00x.gomoviesdroid.player.PlaybackRepository
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class GomoviesModule(private val context: Context) {
  @Singleton
  @Provides
  fun configurationRepository(): ConfigurationRepository = SharedPreferencesConfigurationRepository(context)

  @Singleton
  @Provides
  fun playbackRepository(): PlaybackRepository = AAPlaybackRepository()

  @Singleton
  @Provides
  fun apiService(configRepository: ConfigurationRepository): GomoviesService {
    val retrofit = Retrofit.Builder()
        .baseUrl(configRepository.retrieve().baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
    val api = retrofit.create(GomoviesApi::class.java)
    return DefaultGomoviesService(api)
  }

  @Singleton
  @Provides
  fun gson(): Gson = GsonBuilder().create()

  @Singleton
  @Provides
  fun errorHandler(gson: Gson): ErrorHandler = DefaultErrorHandler(gson)
}
