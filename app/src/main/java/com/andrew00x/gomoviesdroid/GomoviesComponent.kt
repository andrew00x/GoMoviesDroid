package com.andrew00x.gomoviesdroid

import com.andrew00x.gomoviesdroid.ui.CatalogFragment
import com.andrew00x.gomoviesdroid.ui.PlayerFragment
import com.andrew00x.gomoviesdroid.ui.ConfigurationFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [GomoviesModule::class])
interface GomoviesComponent {
  fun injectInto(catalog: CatalogFragment)
  fun injectInto(player: PlayerFragment)
  fun injectInto(config: ConfigurationFragment)
}