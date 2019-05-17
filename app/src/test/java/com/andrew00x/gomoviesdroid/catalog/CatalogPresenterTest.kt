package com.andrew00x.gomoviesdroid.catalog

import com.andrew00x.gomoviesdroid.*
import com.andrew00x.gomoviesdroid.player.PlayerModel
import io.reactivex.Single
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import java.util.concurrent.TimeUnit
import org.mockito.Mockito.`when` as whenInvoke

@RunWith(MockitoJUnitRunner::class)
class CatalogPresenterTest {
  @Rule
  @JvmField
  val schedulers = SchedulersSetup()

  @Mock lateinit var catalog: CatalogModel
  @Mock lateinit var player: PlayerModel
  @Mock lateinit var errorHandler: ErrorHandler
  @Mock lateinit var view: CatalogView
  @Mock lateinit var events: CatalogEventSource
  @InjectMocks lateinit var presenter: CatalogPresenter

  private lateinit var changeSearchField: Event<String>
  private lateinit var clickClearSearchField: Event<Any>
  private lateinit var clickMovie: Event<Movie>

  @Before
  fun setup() {
    changeSearchField = Event()
    clickClearSearchField = Event()
    clickMovie = Event()

    whenInvoke(catalog.list()).thenReturn(Single.just(listOf()))
    whenInvoke(events.changeSearchField()).thenReturn(changeSearchField)
    whenInvoke(events.clickClearSearchField()).thenReturn(clickClearSearchField)
    whenInvoke(events.clickMovie()).thenReturn(clickMovie)
  }

  @After
  fun cleanup() {
    presenter.detach()
  }

  @Test
  fun `list movies on start`() {
    val movies = listOf(
        Movie(1, "gladiator.mkv", "/movies/gladiator.mkv", "movies_1", true),
        Movie(2, "brave heart.mkv", "/movies/brave heart.mkv", "movies_1", true)
    )
    whenInvoke(catalog.list()).thenReturn(Single.just(movies))

    presenter.attach(view, events)

    val inOrder = Mockito.inOrder(view)
    inOrder.verify(view).showLoader()
    inOrder.verify(view).hideLoader()
    inOrder.verify(view).showMovies(movies)
  }

  @Test
  fun `search movies`() {
    presenter.attach(view, events)
    reset(view)
    val movies = listOf(Movie(2, "brave heart.mkv", "/movies/brave heart.mkv", "movies_1", true))
    whenInvoke(catalog.search("brave")).thenReturn(Single.just(movies))

    changeSearchField.send("brave")
    schedulers.computationScheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS)
    schedulers.computationScheduler.triggerActions()

    val inOrder = Mockito.inOrder(view)
    inOrder.verify(view).showLoader()
    inOrder.verify(view).hideLoader()
    inOrder.verify(view).showMovies(movies)

  }

  @Test
  fun `list movies when search field is empty`() {
    presenter.attach(view, events)
    reset(view)
    val movies = listOf(
        Movie(1, "gladiator.mkv", "/movies/gladiator.mkv", "movies_1", true),
        Movie(2, "brave heart.mkv", "/movies/brave heart.mkv", "movies_1", true)
    )
    whenInvoke(catalog.list()).thenReturn(Single.just(movies))

    changeSearchField.send("")
    schedulers.computationScheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS)
    schedulers.computationScheduler.triggerActions()

    val inOrder = Mockito.inOrder(view)
    inOrder.verify(view).showLoader()
    inOrder.verify(view).hideLoader()
    inOrder.verify(view).showMovies(movies)
  }

  @Test
  fun `show clear search field button when search field is not empty`() {
    presenter.attach(view, events)
    reset(view)
    changeSearchField.send("something")
    verify(view).showClearSearchButton()
  }

  @Test
  fun `do not show clear search field button when search field is empty`() {
    presenter.attach(view, events)
    reset(view)
    changeSearchField.send("")
    verify(view, never()).showClearSearchButton()
    verify(view).hideClearSearchButton()
  }

  @Test
  fun `start to play movie when click on it`() {
    presenter.attach(view, events)
    reset(view)
    val movie = Movie(1, "gladiator.mkv", "/movies/gladiator.mkv", "movies_1", true)
    val status = PlayerStatus(file = "/movies/gladiator.mkv")
    whenInvoke(player.play("/movies/gladiator.mkv")).thenReturn(Single.just(status))

    clickMovie.send(movie)

    verify(player).play("/movies/gladiator.mkv")
    val inOrder = Mockito.inOrder(view)
    inOrder.verify(view).showLoader()
    inOrder.verify(view).hideLoader()
    inOrder.verify(view).onPlaybackStarted()
  }

  @Test
  fun `notify ErrorHandler when error occurred while list movies`() {
    val err = RuntimeException("failed")
    whenInvoke(catalog.list()).thenReturn(Single.error(err))
    presenter.attach(view, events)

    val inOrder = Mockito.inOrder(view, errorHandler)
    inOrder.verify(view).showLoader()
    inOrder.verify(view).hideLoader()
    inOrder.verify(errorHandler).handleError(view, err)
  }

  @Test
  fun `notify ErrorHandler when error occurred while search movies`() {
    presenter.attach(view, events)
    reset(view)
    val err = RuntimeException("failed")
    whenInvoke(catalog.search("brave")).thenReturn(Single.error(err))

    changeSearchField.send("brave")
    schedulers.computationScheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS)
    schedulers.computationScheduler.triggerActions()

    val inOrder = Mockito.inOrder(view, errorHandler)
    inOrder.verify(view).showLoader()
    inOrder.verify(view).hideLoader()
    inOrder.verify(errorHandler).handleError(view, err)
  }

  @Test
  fun `notify ErrorHandler when error occurred while start play movie`() {
    presenter.attach(view, events)
    reset(view)
    val movie = Movie(1, "gladiator.mkv", "/movies/gladiator.mkv", "movies_1", true)
    val err = RuntimeException("failed")
    whenInvoke(player.play("/movies/gladiator.mkv")).thenReturn(Single.error(err))

    clickMovie.send(movie)

    val inOrder = Mockito.inOrder(view, errorHandler)
    inOrder.verify(view).showLoader()
    inOrder.verify(view).hideLoader()
    inOrder.verify(errorHandler).handleError(view, err)
  }

  @Test
  fun `clear search field on clear event`() {
    presenter.attach(view, events)
    reset(view)
    clickClearSearchField.send(Unit)
    verify(view).clearSearchField()
  }
}