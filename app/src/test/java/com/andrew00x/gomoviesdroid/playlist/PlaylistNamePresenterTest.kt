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
class PlaylistNamePresenterTest {
  @Mock private lateinit var model: PlaylistModel
  @Mock private lateinit var errorHandler: ErrorHandler
  @Mock private lateinit var view: PlaylistNameView
  @InjectMocks private lateinit var underTest: PlaylistNamePresenter
  private lateinit var clickOnSave: Event<Any>

  @Before fun setup() {
    clickOnSave = Event()
    whenInvoke(view.clickOnSave()).thenReturn(clickOnSave)
  }

  @Test fun `sets playlist name in view when attached`() {
    val playlist = mock(Playlist::class.java)
    whenInvoke(playlist.name).thenReturn("my playlist")
    underTest.playlist = playlist
    underTest.attach(view)

    verify(view).setName("my playlist")
  }

  @Test fun `updates playlist name when save is clicked`() {
    val playlist = mock(Playlist::class.java)
    whenInvoke(playlist.name).thenReturn("my playlist")
    whenInvoke(view.getName()).thenReturn("new playlist name")
    underTest.playlist = playlist
    underTest.attach(view)

    clickOnSave.send(Unit)

    val inOrder = Mockito.inOrder(playlist, model)
    inOrder.verify(playlist).name = "new playlist name"
    inOrder.verify(model).save(playlist)
  }

  @Test fun `notifies about error occurred while save playlist`() {
    val playlist = mock(Playlist::class.java)
    whenInvoke(playlist.name).thenReturn("my playlist")
    val error = RuntimeException()
    whenInvoke(model.save(playlist)).thenThrow(error)
    underTest.playlist = playlist
    underTest.attach(view)

    clickOnSave.send(Unit)

    verify(errorHandler).handleError(view, error)
  }
}
