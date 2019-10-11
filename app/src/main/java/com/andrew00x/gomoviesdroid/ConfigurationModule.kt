package com.andrew00x.gomoviesdroid

import com.andrew00x.gomoviesdroid.config.ConfigurationListener
import com.andrew00x.gomoviesdroid.config.ConfigurationModel
import com.andrew00x.gomoviesdroid.config.ConfigurationPresenter
import com.andrew00x.gomoviesdroid.ui.MainActivity
import dagger.Module
import dagger.Provides

@ConfigurationScope
@Module
class ConfigurationModule(private val activity: MainActivity) {
  @ConfigurationScope
  @Provides
  fun configListener(): ConfigurationListener = activity

  @ConfigurationScope
  @Provides
  fun configurationPresenter(model: ConfigurationModel, listener: ConfigurationListener, errorHandler: ErrorHandler): ConfigurationPresenter =
      ConfigurationPresenter(model, listener, errorHandler)
}
