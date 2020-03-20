package com.andrew00x.gomoviesdroid.catalog

import com.andrew00x.gomoviesdroid.ErrorHandler
import com.andrew00x.gomoviesdroid.Event
import com.andrew00x.gomoviesdroid.SchedulersSetup
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
class CatalogPresenterTest {
  @Rule
  @JvmField
  val schedulers = SchedulersSetup()

  @Mock lateinit var catalogModel: CatalogModel
  @Mock lateinit var catalogView: CatalogView
  @Mock lateinit var errorHandler: ErrorHandler
  @InjectMocks lateinit var underTest: CatalogPresenter

  private lateinit var changeSearchField: Event<String>
  private lateinit var clickClearSearchField: Event<Any>
  private lateinit var longClickMovie: Event<Movie>

  @Before fun setup() {
    changeSearchField = Event()
    clickClearSearchField = Event()
    longClickMovie = Event()

    whenInvoke(catalogModel.load()).thenReturn(Single.just(emptyList()))
    whenInvoke(catalogModel.load(ArgumentMatchers.anyString())).thenReturn(Single.just(emptyList()))
    whenInvoke(catalogView.changeSearchField()).thenReturn(changeSearchField)
    whenInvoke(catalogView.clickClearSearchField()).thenReturn(clickClearSearchField)
    whenInvoke(catalogView.longClickMovie()).thenReturn(longClickMovie)
    whenInvoke(catalogView.getSearchField()).thenReturn("")
  }

  @Test fun `lists all movies when search field is empty`() {
    val movies = listOf(
        Movie(1, "gladiator.mkv", "/movies/gladiator.mkv", "movies_1", available = true, detailsAvailable = false),
        Movie(2, "brave heart.mkv", "/movies/brave heart.mkv", "movies_1", available = true, detailsAvailable = false)
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
        Movie(2, "brave heart.mkv", "/movies/brave heart.mkv", "movies_1", available = true, detailsAvailable = false)
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

  @Test fun `shows movie details when it is long clicked`() {
    val movie = Movie(2, "brave heart.mkv", "/movies/brave heart.mkv", "movies_1", available = true, detailsAvailable = true)
    underTest.attach(catalogView)

    longClickMovie.send(movie)

    verify(catalogView).showDetailsFor(movie)
  }
}
