package com.andrew00x.gomoviesdroid.catalog

import com.andrew00x.gomoviesdroid.ErrorHandler
import com.andrew00x.gomoviesdroid.Event
import com.andrew00x.gomoviesdroid.SchedulersSetup
import com.andrew00x.gomoviesdroid.config.Configuration
import com.andrew00x.gomoviesdroid.config.ConfigurationModel
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.Mockito.`when` as whenInvoke

@RunWith(MockitoJUnitRunner::class)
class MovieDetailsPresenterTest {
  @Rule
  @JvmField
  val schedulers = SchedulersSetup()

  @Mock lateinit var catalog: CatalogModel
  @Mock lateinit var config: ConfigurationModel
  @Mock lateinit var errorHandler: ErrorHandler
  @Spy val movie: Movie = Movie(1, "brave heart.mkv", "/movies/brave heart.mkv", "movies_1", available = true, detailsAvailable = true)
  @InjectMocks lateinit var underTest: MovieDetailsPresenter
  @Mock lateinit var view: MovieDetailsView
  private lateinit var changeLanguage: Event<String>

  @Before fun setup() {
    changeLanguage = Event()
    whenInvoke(config.get()).thenReturn(Configuration("localhost", 8000, setOf("en")))
    whenInvoke(view.changeLanguage()).thenReturn(changeLanguage)
  }

  @Test fun `shows movie details`() {
    val details = MovieDetails(originalTitle = "Brave Heart", overview = "", genres = listOf("Biography", "Drama", "History"), releaseDate = "1995", budget = 10000000, revenue = 10000000, runtime = 123, posterUrl = "")
    whenInvoke(catalog.loadDetails(movie.id, "en")).thenReturn(Single.just(details))
    underTest.attach(view)

    changeLanguage.send("en")

    val inOrder = Mockito.inOrder(view, catalog)
    inOrder.verify(view).showLoader()
    inOrder.verify(catalog).loadDetails(movie.id, "en")
    inOrder.verify(view).hideLoader()
    inOrder.verify(view).setDetails(details, "en")
  }

  @Test fun `notifies about error occurred while load movie details`() {
    val error = RuntimeException()
    whenInvoke(catalog.loadDetails(movie.id, "en")).thenReturn(Single.error(error))
    underTest.attach(view)

    changeLanguage.send("en")

    val inOrder = Mockito.inOrder(view, catalog, errorHandler)
    inOrder.verify(view).showLoader()
    inOrder.verify(catalog).loadDetails(movie.id, "en")
    inOrder.verify(view).hideLoader()
    inOrder.verify(errorHandler).handleError(view, error)
  }
}
