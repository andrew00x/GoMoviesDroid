package com.andrew00x.gomoviesdroid.playlist

import com.andrew00x.gomoviesdroid.ui.BaseView
import io.reactivex.Observable

interface PlaylistView : BaseView {
  fun clickOnAddItems(): Observable<Any>
  fun setPlaylist(playlist: List<PlaylistItem>)
  fun clickOnDelete(): Observable<PlaylistItem>
  fun startAddItems()
}
