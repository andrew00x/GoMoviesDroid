package com.andrew00x.gomoviesdroid.player

import com.andrew00x.gomoviesdroid.*
import com.andrew00x.gomoviesdroid.catalog.CatalogModel
import com.andrew00x.gomoviesdroid.catalog.CatalogView
import com.andrew00x.gomoviesdroid.catalog.Movie
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import java.lang.RuntimeException
import java.util.concurrent.TimeUnit
import org.mockito.Mockito.`when` as whenInvoke

@RunWith(MockitoJUnitRunner::class)
class PlayerCatalogPresenterTest {
  @Rule
  @JvmField
  val schedulers = SchedulersSetup()

  @Mock lateinit var catalogModel: CatalogModel
  @Mock lateinit var playerModel: PlayerModel
  @Mock lateinit var catalogView: CatalogView
  @Mock lateinit var errorHandler: ErrorHandler
  @InjectMocks lateinit var underTest: PlayerCatalogPresenter

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
    whenInvoke(catalogModel.load(ArgumentMatchers.anyString())).thenReturn(Single.just(emptyList()))
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

  @Test fun `starts playing movie when it is long clicked`() {
    val movie = Movie(2, "brave heart.mkv", "/movies/brave heart.mkv", "movies_1", available = true)
    whenInvoke(playerModel.play(movie.file)).thenReturn(Single.just(PlayerStatus(file = movie.file, stopped = false)))
    underTest.attach(catalogView)

    clickMovie.send(movie)

    val inOrder = Mockito.inOrder(catalogView, catalogModel, playerModel)
    inOrder.verify(catalogView).showLoader()
    inOrder.verify(playerModel).play(movie.file)
    inOrder.verify(catalogView).hideLoader()
  }

  @Test fun `notifies about error occurred while start playing movie`() {
    val movie = Movie(2, "brave heart.mkv", "/movies/brave heart.mkv", "movies_1", available = true)
    val error = RuntimeException()
    whenInvoke(playerModel.play(movie.file)).thenReturn(Single.error(error))
    underTest.attach(catalogView)

    clickMovie.send(movie)

    val inOrder = Mockito.inOrder(catalogView, catalogModel, playerModel, errorHandler)
    inOrder.verify(catalogView).showLoader()
    inOrder.verify(playerModel).play(movie.file)
    inOrder.verify(catalogView).hideLoader()
    inOrder.verify(errorHandler).handleError(catalogView, error)
  }

  @Test fun `shows movie details when it is long clicked`() {
    val movie = Movie(2, "brave heart.mkv", "/movies/brave heart.mkv", "movies_1", available = true)
    underTest.attach(catalogView)

    longClickMovie.send(movie)

    verify(catalogView).showDetailsFor(movie)
  }
}
