package com.andrew00x.gomoviesdroid.config

import io.reactivex.Single
import javax.inject.Inject

class ConfigurationModel @Inject constructor (private val configurationRepository: ConfigurationRepository) {
  fun read(): Single<Configuration> {
    return Single.fromCallable { configurationRepository.retrieve() }
  }

  fun save(config: Configuration): Single<Any> {
    return Single.fromCallable { configurationRepository.save(config) }
  }
}

