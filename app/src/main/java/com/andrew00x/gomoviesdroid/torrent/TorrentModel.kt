package com.andrew00x.gomoviesdroid.torrent

import android.util.Base64
import com.andrew00x.gomoviesdroid.GomoviesService
import com.andrew00x.gomoviesdroid.TorrentDownload
import com.andrew00x.gomoviesdroid.TorrentFile
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

data class TorrentDownloadItem(
    var data: TorrentDownload,
    val position: Int
)

@Singleton
class TorrentModel @Inject constructor(
    private val service: GomoviesService
) {
  fun list(): Single<List<TorrentDownloadItem>> =
      service.listTorrents().map { torrents -> torrents.mapIndexed { index, torrent -> TorrentDownloadItem(torrent, index) } }

  fun toggleStartStop(torrent: TorrentDownloadItem): Single<TorrentDownloadItem> {
    return (if (torrent.data.stopped) service.startDownload(torrent.data) else service.stopDownload(torrent.data))
        .map { data -> torrent.apply { this.data = data } }
  }

  fun delete(torrent: TorrentDownloadItem) =
      service.deleteTorrent(torrent.data).map { torrents -> torrents.mapIndexed { index, torrent -> TorrentDownloadItem(torrent, index) } }

  fun addTorrent(torrent: ByteArray): Completable {
    return service.addTorrent(TorrentFile(Base64.encodeToString(torrent, Base64.NO_WRAP)))
  }
}
