package com.andrew00x.gomoviesdroid

import com.andrew00x.gomoviesdroid.ui.config.ConfigurationFragment
import dagger.Subcomponent
import javax.inject.Scope

@Scope
@Retention
annotation class ConfigurationScope

@ConfigurationScope
@Subcomponent(modules = [ConfigurationModule::class])
interface ConfigurationComponent {
  fun inject(fr: ConfigurationFragment)
}