package com.andrew00x.gomoviesdroid.torrent

import android.net.Uri
import com.andrew00x.gomoviesdroid.ui.BaseView
import io.reactivex.Observable

interface TorrentView : BaseView {
  fun setTorrents(torrents: List<TorrentDownloadItem>)
  fun selectTorrentFile()
  fun updateTorrent(torrent: TorrentDownloadItem)
  fun torrentFileSelected(): Observable<Uri>
  fun clickOnItem(): Observable<TorrentDownloadItem>
  fun clickOnDelete(): Observable<TorrentDownloadItem>
  fun clickOnAddTorrent(): Observable<Any>
  fun clickOnRefresh(): Observable<Any>
}
