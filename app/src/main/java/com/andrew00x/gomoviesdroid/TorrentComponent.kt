package com.andrew00x.gomoviesdroid

import com.andrew00x.gomoviesdroid.ui.torrent.TorrentFragment
import dagger.Subcomponent
import javax.inject.Scope

@Scope
@Retention
annotation class TorrentScope

@TorrentScope
@Subcomponent(modules = [TorrentModule::class])
interface TorrentComponent {
  fun inject(fr: TorrentFragment)
}