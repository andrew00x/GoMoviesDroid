package com.andrew00x.gomoviesdroid.playlist

import com.andrew00x.gomoviesdroid.ErrorHandler
import com.andrew00x.gomoviesdroid.Event
import com.andrew00x.gomoviesdroid.SchedulersSetup
import com.andrew00x.gomoviesdroid.catalog.CatalogModel
import com.andrew00x.gomoviesdroid.catalog.CatalogView
import com.andrew00x.gomoviesdroid.catalog.Movie
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import java.lang.RuntimeException
import java.util.concurrent.TimeUnit
import org.mockito.Mockito.`when` as whenInvoke

@RunWith(MockitoJUnitRunner::class)
class PlaylistAppendPresenterTest {
  @Rule
  @JvmField
  val schedulers = SchedulersSetup()

  @Mock lateinit var catalogModel: CatalogModel
  @Mock lateinit var catalogView: CatalogView
  @Mock lateinit var errorHandler: ErrorHandler
  @Mock lateinit var playlistModel: PlaylistModel
  @InjectMocks lateinit var underTest: PlaylistAppendPresenter

  private lateinit var changeSearchField: Event<String>
  private lateinit var clickClearSearchField: Event<Any>
  private lateinit var clickMovie: Event<Movie>
  private lateinit var longClickMovie: Event<Movie>

  @Before fun setup() {
    changeSearchField = Event()
    clickClearSearchField = Event()
    clickMovie = Event()
    longClickMovie = Event()

    whenInvoke(catalogModel.load()).thenReturn(Single.just(emptyList()))
    whenInvoke(catalogModel.load(anyString())).thenReturn(Single.just(emptyList()))
    whenInvoke(catalogView.changeSearchField()).thenReturn(changeSearchField)
    whenInvoke(catalogView.clickClearSearchField()).thenReturn(clickClearSearchField)
    whenInvoke(catalogView.clickMovie()).thenReturn(clickMovie)
    whenInvoke(catalogView.longClickMovie()).thenReturn(longClickMovie)
    whenInvoke(catalogView.getSearchField()).thenReturn("")
  }

  @Test fun `lists all movies when search field is empty`() {
    val movies = listOf(
        Movie(1, "gladiator.mkv", "/movies/gladiator.mkv", "movies_1", available = true),
        Movie(2, "brave heart.mkv", "/movies/brave heart.mkv", "movies_1", available = true)
    )
    whenInvoke(catalogModel.load()).thenReturn(Single.just(movies))

    underTest.attach(catalogView)
    changeSearchField.send("") // behave like RxTextView.textChanges, extra event when attach listener
    changeSearchField.send("")
    schedulers.computationScheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS)
    schedulers.computationScheduler.triggerActions()

    val inOrder = Mockito.inOrder(catalogView, catalogModel)
    inOrder.verify(catalogView).showLoader()
    inOrder.verify(catalogModel).load()
    inOrder.verify(catalogView).hideLoader()
    inOrder.verify(catalogView).setMovies(movies)
  }

  @Test fun `lists movies when search field changed`() {
    val movies = listOf(
        Movie(2, "brave heart.mkv", "/movies/brave heart.mkv", "movies_1", available = true)
    )
    whenInvoke(catalogModel.load("brave")).thenReturn(Single.just(movies))

    underTest.attach(catalogView)
    changeSearchField.send("") // behave like RxTextView.textChanges, extra event when attach listener
    changeSearchField.send("brave")
    schedulers.computationScheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS)
    schedulers.computationScheduler.triggerActions()

    val inOrder = Mockito.inOrder(catalogView, catalogModel)
    inOrder.verify(catalogView).showLoader()
    inOrder.verify(catalogModel).load("brave")
    inOrder.verify(catalogView).hideLoader()
    inOrder.verify(catalogView).setMovies(movies)
  }

  @Test fun `notifies about error occurred while load movies`() {
    val error = RuntimeException()
    whenInvoke(catalogModel.load()).thenReturn(Single.error(error))

    underTest.attach(catalogView)
    changeSearchField.send("") // behave like RxTextView.textChanges, extra event when attach listener
    changeSearchField.send("")
    schedulers.computationScheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS)
    schedulers.computationScheduler.triggerActions()

    val inOrder = Mockito.inOrder(catalogView, catalogModel, errorHandler)
    inOrder.verify(catalogView).showLoader()
    inOrder.verify(catalogModel).load()
    inOrder.verify(catalogView).hideLoader()
    inOrder.verify(errorHandler).handleError(catalogView, error)
  }

  @Test fun `hides clear search field button when field is empty`() {
    underTest.attach(catalogView)
    changeSearchField.send("")
    verify(catalogView).hideClearSearch()
  }

  @Test fun `shows clear search field button when field contains text`() {
    underTest.attach(catalogView)
    changeSearchField.send("any")
    verify(catalogView).showClearSearch()
  }

  @Test fun `clears search field when clear button is clicked`() {
    underTest.attach(catalogView)
    clickClearSearchField.send(Unit)
    verify(catalogView).clearSearchField()
  }

  @Test fun `adds movie in playlist when it is clicked`() {
    val movie = Movie(2, "brave heart.mkv", "/movies/brave heart.mkv", "movies_1", available = true)
    val playlist = mock(Playlist::class.java)
    underTest.playlist = playlist
    underTest.attach(catalogView)

    clickMovie.send(movie)

    verify(playlistModel).addItem(playlist, movie)
  }

  @Test fun `notifies about error occurred while add item in playlist`() {
    val movie = Movie(2, "brave heart.mkv", "/movies/brave heart.mkv", "movies_1", available = true)
    val playlist = mock(Playlist::class.java)
    val error = RuntimeException()
    whenInvoke(playlistModel.addItem(playlist, movie)).thenThrow(error)
    underTest.playlist = playlist
    underTest.attach(catalogView)

    clickMovie.send(movie)

    verify(errorHandler).handleError(catalogView, error)
  }
}
