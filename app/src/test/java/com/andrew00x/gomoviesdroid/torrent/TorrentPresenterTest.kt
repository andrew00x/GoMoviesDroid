package com.andrew00x.gomoviesdroid.torrent

import android.net.Uri
import com.andrew00x.gomoviesdroid.*
import com.andrew00x.gomoviesdroid.file.ContentProvider
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.reset
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations.initMocks
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.mockito.Mockito.`when` as whenInvoke

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26], application = TestApplication::class, manifest = Config.NONE)
class TorrentPresenterTest {
  @Rule
  @JvmField
  val schedulers = SchedulersSetup()

  @Mock private lateinit var view: TorrentView
  @Mock private lateinit var errorHandler: ErrorHandler
  @Mock private lateinit var torrentModel: TorrentModel
  @Mock private lateinit var torrentContentProvider: ContentProvider
  @InjectMocks private lateinit var underTest: TorrentPresenter

  private lateinit var clickOnItem: Event<TorrentDownloadItem>
  private lateinit var clickOnDelete: Event<TorrentDownloadItem>
  private lateinit var clickOnRefresh: Event<Any>
  private lateinit var clickOnAddTorrent: Event<Any>
  private lateinit var torrentFileSelected: Event<Uri>

  @Before fun setup() {
    initMocks(this)
    clickOnItem = Event()
    clickOnDelete = Event()
    clickOnRefresh = Event()
    clickOnAddTorrent = Event()
    torrentFileSelected = Event()
    whenInvoke(view.clickOnItem()).thenReturn(clickOnItem)
    whenInvoke(view.clickOnDelete()).thenReturn(clickOnDelete)
    whenInvoke(view.clickOnAddTorrent()).thenReturn(clickOnAddTorrent)
    whenInvoke(view.clickOnRefresh()).thenReturn(clickOnRefresh)
    whenInvoke(view.torrentFileSelected()).thenReturn(torrentFileSelected)
    whenInvoke(torrentModel.list()).thenReturn(Single.just(listOf()))
  }

  @Test fun `show torrents when attach`() {
    val torrents = listOf(TorrentDownloadItem(TorrentDownload(name = "download 1"), 0))
    whenInvoke(torrentModel.list()).thenReturn(Single.just(torrents))
    underTest.attach(view)
    val inOrder = Mockito.inOrder(view)
    inOrder.verify(view).showLoader()
    inOrder.verify(view).hideLoader()
    inOrder.verify(view).setTorrents(torrents)
  }

  @Test fun `refresh view`() {
    val torrents = listOf(TorrentDownloadItem(TorrentDownload(name = "download 1"), 0))
    whenInvoke(torrentModel.list()).thenReturn(Single.just(torrents))
    underTest.refresh(view)
    val inOrder = Mockito.inOrder(view)
    inOrder.verify(view).showLoader()
    inOrder.verify(view).hideLoader()
    inOrder.verify(view).setTorrents(torrents)
  }

  @Test fun `delete torrent when click on delete`() {
    val torrents = listOf(TorrentDownloadItem(TorrentDownload(name = "download 1"), 0), TorrentDownloadItem(TorrentDownload(name = "download 2"), 1))
    val afterDelete = listOf(TorrentDownloadItem(TorrentDownload(name = "download 2"), 0))
    whenInvoke(torrentModel.list()).thenReturn(Single.just(torrents))
    whenInvoke(torrentModel.delete(torrents[0])).thenReturn(Single.just(afterDelete))
    underTest.attach(view)
    reset(view)

    clickOnDelete.send(torrents[0])

    val inOrder = Mockito.inOrder(view)
    inOrder.verify(view).showLoader()
    inOrder.verify(view).hideLoader()
    inOrder.verify(view).setTorrents(afterDelete)
  }

  @Test fun `toggle start stop torrent when click on it`() {
    val torrents = listOf(TorrentDownloadItem(TorrentDownload(name = "download 1"), 0))
    whenInvoke(torrentModel.list()).thenReturn(Single.just(torrents))
    whenInvoke(torrentModel.toggleStartStop(torrents[0])).thenReturn(Single.just(torrents[0]))
    underTest.attach(view)
    reset(view)

    clickOnItem.send(torrents[0])

    val inOrder = Mockito.inOrder(view, torrentModel)
    inOrder.verify(view).showLoader()
    inOrder.verify(torrentModel).toggleStartStop(torrents[0])
    inOrder.verify(view).hideLoader()
  }

  @Test fun `handle error occurred while attach`() {
    val error = RuntimeException()
    whenInvoke(torrentModel.list()).thenReturn(Single.error(error))
    underTest.attach(view)

    val inOrder = Mockito.inOrder(view, errorHandler)
    inOrder.verify(view).showLoader()
    inOrder.verify(view).hideLoader()
    inOrder.verify(errorHandler).handleError(view, error)
  }

  @Test fun `handle error occurred while refresh`() {
    val error = RuntimeException()
    whenInvoke(torrentModel.list()).thenReturn(Single.error(error))
    underTest.refresh(view)
    val inOrder = Mockito.inOrder(view, errorHandler)
    inOrder.verify(view).showLoader()
    inOrder.verify(view).hideLoader()
    inOrder.verify(errorHandler).handleError(view, error)
  }

  @Test fun `handle error occurred while delete`() {
    val error = RuntimeException()
    val torrents = listOf(TorrentDownloadItem(TorrentDownload(name = "download 1"), 0))
    whenInvoke(torrentModel.list()).thenReturn(Single.just(torrents))
    whenInvoke(torrentModel.delete(torrents[0])).thenReturn(Single.error(error))
    underTest.attach(view)
    reset(view)

    clickOnDelete.send(torrents[0])

    val inOrder = Mockito.inOrder(view, errorHandler)
    inOrder.verify(view).showLoader()
    inOrder.verify(view).hideLoader()
    inOrder.verify(errorHandler).handleError(view, error)
  }

  @Test fun `handle error occurred while toggle start stop`() {
    val error = RuntimeException()
    val torrents = listOf(TorrentDownloadItem(TorrentDownload(name = "download 1"), 0))
    whenInvoke(torrentModel.list()).thenReturn(Single.just(torrents))
    whenInvoke(torrentModel.toggleStartStop(torrents[0])).thenReturn(Single.error(error))
    underTest.attach(view)
    reset(view)

    clickOnItem.send(torrents[0])

    val inOrder = Mockito.inOrder(view, errorHandler)
    inOrder.verify(view).showLoader()
    inOrder.verify(view).hideLoader()
    inOrder.verify(errorHandler).handleError(view, error)
  }

  @Test fun `start download when torrent file selected`() {
    val torrentFileUri = Uri.parse("download-1.torrent")
    val torrentFile = "download 1".toByteArray()
    whenInvoke(torrentContentProvider.getBytes(torrentFileUri)).thenReturn(torrentFile)
    whenInvoke(torrentModel.addTorrent(torrentFile)).thenReturn(Completable.complete())
    underTest.attach(view)
    reset(view)
    val torrents = listOf(TorrentDownloadItem(TorrentDownload(name = "download 1"), 0))
    whenInvoke(torrentModel.list()).thenReturn(Single.just(torrents))

    torrentFileSelected.send(torrentFileUri)

    val inOrder = Mockito.inOrder(view, torrentModel)
    inOrder.verify(view).showLoader()
    inOrder.verify(torrentModel).addTorrent(torrentFile)
    inOrder.verify(view).hideLoader()
    verify(view).setTorrents(torrents)
  }

  @Test fun `handle error occurred while add torrent`() {
    val torrentFileUri = Uri.parse("download-1.torrent")
    val torrentFile = "download 1".toByteArray()
    whenInvoke(torrentContentProvider.getBytes(torrentFileUri)).thenReturn(torrentFile)
    val error = RuntimeException()
    whenInvoke(torrentModel.addTorrent(torrentFile)).thenReturn(Completable.error(error))
    underTest.attach(view)
    reset(view)

    torrentFileSelected.send(torrentFileUri)

    val inOrder = Mockito.inOrder(view, errorHandler)
    inOrder.verify(view).showLoader()
    inOrder.verify(view).hideLoader()
    inOrder.verify(errorHandler).handleError(view, error)
  }
}
