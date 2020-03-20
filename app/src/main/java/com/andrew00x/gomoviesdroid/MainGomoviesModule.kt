package com.andrew00x.gomoviesdroid

import android.content.ContentResolver
import android.content.Context
import com.andrew00x.gomoviesdroid.config.ConfigurationModel
import com.andrew00x.gomoviesdroid.file.ContentProvider
import com.andrew00x.gomoviesdroid.file.DefaultContentProvider
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit.SECONDS
import javax.inject.Singleton

@Module
class MainGomoviesModule(private val context: Context) {
  @Singleton
  @Provides
  fun context(): Context = context

  @Singleton
  @Provides
  fun apiService(config: ConfigurationModel, okHttpClient: OkHttpClient): GomoviesService {
    val builder = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    try {
      builder.baseUrl(config.get().baseUrl)
    } catch (e: IllegalArgumentException) {
      // prevent activity fail without any chance to update previously broken configuration
      builder.baseUrl("http://10.0.2.2:8000")
    }
    val retrofit = builder.client(okHttpClient).build()
    val api = retrofit.create(GomoviesApi::class.java)
    return DefaultGomoviesService(api)
  }

  @Singleton
  @Provides
  fun gson(): Gson = GsonBuilder().create()

  @Singleton
  @Provides
  fun errorHandler(gson: Gson): ErrorHandler = DefaultErrorHandler(gson)

  @Singleton
  @Provides
  fun urlValidator(): UrlValidator = DefaultUrlValidator()

  @Singleton
  @Provides
  fun okHttpClient(): OkHttpClient = OkHttpClient.Builder()
      .connectTimeout(60, SECONDS)
      .readTimeout(60, SECONDS)
      .writeTimeout(60, SECONDS)
      .build()

  @Singleton
  @Provides
  fun contentResolver(): ContentResolver = context.contentResolver

  @Singleton
  @Provides
  fun contentProvider(contentResolver: ContentResolver): ContentProvider = DefaultContentProvider(contentResolver)
}
