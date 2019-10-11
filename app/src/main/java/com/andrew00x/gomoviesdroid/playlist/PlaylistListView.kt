package com.andrew00x.gomoviesdroid.playlist

import com.andrew00x.gomoviesdroid.ui.BaseView
import io.reactivex.Observable

interface PlaylistListView: BaseView {
  fun clickOnCreate(): Observable<Any>
  fun setPlaylistList(playlistList: List<Playlist>)
  fun clickOnPlaylist(): Observable<Playlist>
  fun longClickOnPlaylist(): Observable<Playlist>
  fun showPlaylist(playlist: Playlist)
  fun editPlaylist(playlist: Playlist)
  fun clickOnEdit(): Observable<Playlist>
  fun clickOnDelete(): Observable<Playlist>
}
