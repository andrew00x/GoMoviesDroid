package com.andrew00x.gomoviesdroid.playlist

import com.andrew00x.gomoviesdroid.ErrorHandler
import com.andrew00x.gomoviesdroid.Event
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import java.lang.RuntimeException
import org.mockito.Mockito.`when` as whenInvoke

@RunWith(MockitoJUnitRunner::class)
class PlaylistPresenterTest {
  @Mock lateinit var playlistModel: PlaylistModel
  @Mock lateinit var errorHandler: ErrorHandler
  @Mock lateinit var playlistView: PlaylistView
  @InjectMocks lateinit var underTest: PlaylistPresenter

  private lateinit var clickOnAddItems: Event<Any>
  private lateinit var clickOnDelete: Event<PlaylistItem>

  @Before fun setup() {
    clickOnAddItems = Event()
    clickOnDelete = Event()
    whenInvoke(playlistView.clickOnAddItems()).thenReturn(clickOnAddItems)
    whenInvoke(playlistView.clickOnDelete()).thenReturn(clickOnDelete)
  }

  @Test fun `shows playlist items when attach view`() {
    val playlist = mock(Playlist::class.java)
    val item1 = mock(PlaylistItem::class.java)
    val item2 = mock(PlaylistItem::class.java)
    whenInvoke(playlistModel.getAll(playlist)).thenReturn(listOf(item1, item2))
    underTest.playlist = playlist
    underTest.attach(playlistView)


    verify(playlistView).setPlaylist(listOf(item1, item2))
  }

  @Test fun `opens add playlist items view when add items button is clicked`() {
    underTest.attach(playlistView)
    clickOnAddItems.send(Unit)
    verify(playlistView).startAddItems()
  }

  @Test fun `deletes item from playlist when delete button is clicked`() {
    val playlist = mock(Playlist::class.java)
    val item1 = mock(PlaylistItem::class.java)
    val item2 = mock(PlaylistItem::class.java)
    whenInvoke(playlistModel.delete(playlist, item1)).thenReturn(listOf(item2))
    underTest.playlist = playlist
    underTest.attach(playlistView)

    clickOnDelete.send(item1)

    val inOrder = Mockito.inOrder(playlistModel, playlistView)
    inOrder.verify(playlistModel).delete(playlist, item1)
    inOrder.verify(playlistView).setPlaylist(listOf(item2))
  }

  @Test fun `notifies about error occurred while load playlist items`() {
    val playlist = mock(Playlist::class.java)
    val error = RuntimeException()
    whenInvoke(playlistModel.getAll(playlist)).thenThrow(error)
    underTest.playlist = playlist
    underTest.attach(playlistView)


    verify(errorHandler).handleError(playlistView, error)
  }

  @Test fun `notifies about error occurred while delete playlist item`() {
    val playlist = mock(Playlist::class.java)
    val item = mock(PlaylistItem::class.java)
    val error = RuntimeException()
    whenInvoke(playlistModel.delete(playlist, item)).thenThrow(error)
    underTest.playlist = playlist
    underTest.attach(playlistView)

    clickOnDelete.send(item)

    verify(errorHandler).handleError(playlistView, error)
  }

  @Test fun `refreshes view`() {
    val playlist = mock(Playlist::class.java)
    val item = mock(PlaylistItem::class.java)
    whenInvoke(playlistModel.getAll(playlist)).thenReturn(listOf(item))
    underTest.playlist = playlist

    underTest.refresh(playlistView)

    verify(playlistView).setPlaylist(listOf(item))
  }

  @Test fun `notifies about error occurred while refresh view`() {
    val playlist = mock(Playlist::class.java)
    val error = RuntimeException()
    whenInvoke(playlistModel.getAll(playlist)).thenThrow(error)
    underTest.playlist = playlist

    underTest.refresh(playlistView)

    verify(errorHandler).handleError(playlistView, error)
  }
}
