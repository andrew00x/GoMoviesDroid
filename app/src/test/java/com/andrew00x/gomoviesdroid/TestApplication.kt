package com.andrew00x.gomoviesdroid

import com.activeandroid.ActiveAndroid
import com.activeandroid.Configuration
import com.andrew00x.gomoviesdroid.player.AAPlayback
import com.andrew00x.gomoviesdroid.playlist.Playlist
import com.andrew00x.gomoviesdroid.playlist.PlaylistItem

class TestApplication : android.app.Application() {

  override fun onCreate() {
    super.onCreate()
    val config = Configuration.Builder(this)
        .addModelClasses(
            AAPlayback::class.java,
            Playlist::class.java,
            PlaylistItem::class.java
        ).create()
    ActiveAndroid.initialize(config)
  }

  override fun onTerminate() {
    ActiveAndroid.dispose()
    super.onTerminate()
  }
}
