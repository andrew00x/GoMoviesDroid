package com.andrew00x.gomoviesdroid.torrent

import android.util.Base64
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.andrew00x.gomoviesdroid.GomoviesService
import com.andrew00x.gomoviesdroid.TestApplication
import com.andrew00x.gomoviesdroid.TorrentDownload
import com.andrew00x.gomoviesdroid.TorrentFile
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations.initMocks
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.mockito.Mockito.`when` as whenInvoke

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26], application = TestApplication::class, manifest = Config.NONE)
class TorrentModelTest {
  @Mock private lateinit var service: GomoviesService
  @InjectMocks private lateinit var underTest: TorrentModel

  @Before fun setup() {
    initMocks(this)
  }

  @Test fun `list torrents`() {
    val torrents = listOf(TorrentDownload(name = "download 1"))
    whenInvoke(service.listTorrents()).thenReturn(Single.just(torrents))
    assertThat(underTest.list().blockingGet()).isEqualTo(torrents.mapIndexed{ index, data -> TorrentDownloadItem(data, index) })
  }

  @Test fun `start download`() {
    val torrentBefore = TorrentDownload(name = "download 1", stopped = true)
    val torrentAfter = TorrentDownload(name = "download 1", stopped = false)
    whenInvoke(service.startDownload(torrentBefore)).thenReturn(Single.just(torrentAfter))
    val item = TorrentDownloadItem(torrentBefore, 0)
    underTest.toggleStartStop(item).blockingGet()
    assertThat(item.data).isEqualTo(torrentAfter)
  }

  @Test fun `stop download`() {
    val torrentBefore = TorrentDownload(name = "download 1", stopped = false)
    val torrentAfter = TorrentDownload(name = "download 1", stopped = true)
    whenInvoke(service.stopDownload(torrentBefore)).thenReturn(Single.just(torrentAfter))
    val item = TorrentDownloadItem(torrentBefore, 0)
    underTest.toggleStartStop(item).blockingGet()
    assertThat(item.data).isEqualTo(torrentAfter)
  }

  @Test fun `delete download`() {
    val delete = TorrentDownload("download 1")
    val afterDelete = listOf(TorrentDownload("download 2"))
    whenInvoke(service.deleteTorrent(delete)).thenReturn(Single.just(afterDelete))
    assertThat(underTest.delete(TorrentDownloadItem(delete, 0)).blockingGet()).isEqualTo(afterDelete.mapIndexed{ index, data -> TorrentDownloadItem(data, index) })
  }

  @Test fun `add torrent`() {
    val torrentContent = "download 1".toByteArray()
    underTest.addTorrent(torrentContent)
    val torrentFile = TorrentFile(Base64.encodeToString(torrentContent, Base64.NO_WRAP))
    verify(service).addTorrent(torrentFile)
  }

}
