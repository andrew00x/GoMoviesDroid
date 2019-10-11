package com.andrew00x.gomoviesdroid

import com.andrew00x.gomoviesdroid.ui.player.PlayerFragment
import com.andrew00x.gomoviesdroid.ui.queue.QueueFragment
import com.andrew00x.gomoviesdroid.ui.catalog.CatalogFragment
import com.andrew00x.gomoviesdroid.ui.playlist.PlaylistAppendFragment
import com.andrew00x.gomoviesdroid.ui.playlist.PlaylistFragment
import com.andrew00x.gomoviesdroid.ui.playlist.PlaylistListFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [MainGomoviesModule::class])
interface MainGomoviesComponent {
  fun inject(fr: PlayerFragment)
  fun inject(fr: QueueFragment)
  fun inject(fr: PlaylistListFragment)
  fun inject(fr: CatalogFragment)
  fun inject(fr: PlaylistAppendFragment)
  fun inject(fr: PlaylistFragment)
  fun plus(ext: ConfigurationModule): ConfigurationComponent
  fun plus(ext: MovieDetailsModule): MovieDetailsComponent
}