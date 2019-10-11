package com.andrew00x.gomoviesdroid.playlist

import com.andrew00x.gomoviesdroid.ErrorHandler
import com.andrew00x.gomoviesdroid.Event
import com.andrew00x.gomoviesdroid.SchedulersSetup
import com.andrew00x.gomoviesdroid.queue.QueueItem
import com.andrew00x.gomoviesdroid.queue.QueueModel
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.Mockito.`when` as whenInvoke

@RunWith(MockitoJUnitRunner::class)
class PlaylistListPresenterTest {
  @Rule
  @JvmField
  val schedulers = SchedulersSetup()

  @Mock private lateinit var view: PlaylistListView
  @Mock private lateinit var playlistModel: PlaylistModel
  @Mock private lateinit var queueModel: QueueModel
  @Mock private lateinit var errorHandler: ErrorHandler
  @InjectMocks private lateinit var underTest: PlaylistListPresenter
  private lateinit var clickOnPlaylist: Event<Playlist>
  private lateinit var longClickOnPlaylist: Event<Playlist>
  private lateinit var clickOnEdit: Event<Playlist>
  private lateinit var clickOnCreate: Event<Any>
  private lateinit var clickOnDelete: Event<Playlist>

  @Before fun setup() {
    clickOnPlaylist = Event()
    longClickOnPlaylist = Event()
    clickOnEdit = Event()
    clickOnCreate = Event()
    clickOnDelete = Event()
    whenInvoke(view.clickOnPlaylist()).thenReturn(clickOnPlaylist)
    whenInvoke(view.longClickOnPlaylist()).thenReturn(longClickOnPlaylist)
    whenInvoke(view.clickOnEdit()).thenReturn(clickOnEdit)
    whenInvoke(view.clickOnCreate()).thenReturn(clickOnCreate)
    whenInvoke(view.clickOnDelete()).thenReturn(clickOnDelete)
  }

  @Test fun `shows list of playlist when attach`() {
    val playlistList = listOf(mock(Playlist::class.java), mock(Playlist::class.java))
    whenInvoke(playlistModel.getAll()).thenReturn(playlistList)
    underTest.attach(view)

    verify(view).setPlaylistList(playlistList)
  }

  @Test fun `opens playlist when playlist is clicked`() {
    val playlist = mock(Playlist::class.java)
    underTest.attach(view)
    clickOnPlaylist.send(playlist)
    verify(view).showPlaylist(playlist)
  }

  @Test fun `starts playlist editing when edit button is clicked`() {
    val playlist = mock(Playlist::class.java)
    underTest.attach(view)
    clickOnEdit.send(playlist)
    verify(view).editPlaylist(playlist)
  }

  @Test fun `deletes playlist when delete button is clicked`() {
    val playlist = mock(Playlist::class.java)
    underTest.attach(view)
    clickOnDelete.send(playlist)
    verify(playlistModel).delete(playlist)
  }

  @Test fun `creates new playlist when create button is clicked`() {
    val playlist = mock(Playlist::class.java)
    whenInvoke(playlistModel.getAll()).thenReturn(listOf())
    underTest.attach(view)

    whenInvoke(playlistModel.getAll()).thenReturn(listOf(playlist))
    clickOnCreate.send(Unit)

    val inOrder = Mockito.inOrder(playlistModel, view)
    inOrder.verify(playlistModel).create()
    inOrder.verify(view).setPlaylistList(listOf(playlist))
  }

  @Test fun `notifies about error occurred while retrieve list of playlists`() {
    val error = RuntimeException()
    whenInvoke(playlistModel.getAll()).thenThrow(error)
    underTest.attach(view)

    verify(errorHandler).handleError(view, error)
  }

  @Test fun `notifies about error occurred while delete playlist`() {
    val playlist = mock(Playlist::class.java)
    val error = RuntimeException()
    whenInvoke(playlistModel.delete(playlist)).thenThrow(error)
    underTest.attach(view)

    clickOnDelete.send(playlist)

    verify(errorHandler).handleError(view, error)
  }

  @Test fun `notifies about error occurred while create playlist`() {
    val error = RuntimeException()
    whenInvoke(playlistModel.create()).thenThrow(error)
    underTest.attach(view)

    clickOnCreate.send(Unit)

    verify(errorHandler).handleError(view, error)
  }

  @Test fun `appends playlist in playing queue when playlist is long clicked`() {
    val playlistList = listOf(mock(Playlist::class.java), mock(Playlist::class.java))
    whenInvoke(playlistModel.getAll()).thenReturn(playlistList)
    val movie1 = mock(PlaylistItem::class.java)
    val movie2 = mock(PlaylistItem::class.java)
    whenInvoke(movie1.file).thenReturn("/movies/gladiator.mkv")
    whenInvoke(movie2.file).thenReturn("/movies/brave heart.mkv")
    whenInvoke(playlistModel.getAll(playlistList[0])).thenReturn(listOf(movie1, movie2))
    whenInvoke(queueModel.enqueue(listOf("/movies/gladiator.mkv", "/movies/brave heart.mkv"), true))
        .thenReturn(Single.just(listOf(QueueItem("/movies/gladiator.mkv", 0), QueueItem("/movies/brave heart.mkv", 1))))
    underTest.attach(view)

    longClickOnPlaylist.send(playlistList[0])

    verify(queueModel).enqueue(listOf("/movies/gladiator.mkv", "/movies/brave heart.mkv"), true)
  }
}
