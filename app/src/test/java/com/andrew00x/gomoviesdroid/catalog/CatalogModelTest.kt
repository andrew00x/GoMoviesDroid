package com.andrew00x.gomoviesdroid.catalog

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.andrew00x.gomoviesdroid.GomoviesService
import com.andrew00x.gomoviesdroid.MovieData
import com.andrew00x.gomoviesdroid.MovieDetailsData
import io.reactivex.Single
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.Mockito.`when` as whenInvoke

@RunWith(MockitoJUnitRunner::class)
class CatalogModelTest {
  @Mock lateinit var service: GomoviesService
  @InjectMocks lateinit var underTest: CatalogModel

  @Test fun `searches for movies by title`() {
    val data = listOf(
        MovieData(2, "brave heart.mkv", "/movies/brave heart.mkv", "movies_1", true, MovieDetailsData(originalTitle = "Brave Heart", tmdbId = 3))
    )
    whenInvoke(service.search("brave")).thenReturn(Single.just(data))
    assertThat(underTest.load("brave").blockingGet())
        .isEqualTo(listOf(Movie(2, "brave heart.mkv", "/movies/brave heart.mkv", "movies_1", available = true)))
  }

  @Test fun `lists all movies`() {
    val data = listOf(
        MovieData(1, "gladiator.mkv", "/movies/gladiator.mkv", "movies_1", true, MovieDetailsData(originalTitle = "Gladiator", tmdbId = 4)),
        MovieData(2, "brave heart.mkv", "/movies/brave heart.mkv", "movies_1", true)
    )
    whenInvoke(service.list()).thenReturn(Single.just(data))
    assertThat(underTest.load().blockingGet())
        .isEqualTo(listOf(
            Movie(1, "gladiator.mkv", "/movies/gladiator.mkv", "movies_1", available = true),
            Movie(2, "brave heart.mkv", "/movies/brave heart.mkv", "movies_1", available = true)
        ))
  }

  @Test(expected = RuntimeException::class)
  fun `fails when error occurred while list movies`() {
    val err = RuntimeException("failed")
    whenInvoke(service.list()).thenReturn(Single.error(err))
    underTest.load().blockingGet()
  }

  @Test(expected = RuntimeException::class)
  fun `fails when error occurred while search for movies`() {
    val err = RuntimeException("failed")
    whenInvoke(service.search("something")).thenReturn(Single.error(err))
    underTest.load("something").blockingGet()
  }

  @Test fun `loads movie details`() {
    val detailsData = MovieDetailsData(originalTitle = "Brave Heart", overview = "", genres = listOf("Biography", "Drama", "History"), releaseDate = "1995", budget = 10000000, revenue = 10000000, runtime = 123, posterSmallUrl = "", tmdbId = 102)
    val details = MovieDetails(originalTitle = "Brave Heart", overview = "", genres = listOf("Biography", "Drama", "History"), releaseDate = "1995", budget = 10000000, revenue = 10000000, runtime = 123, posterUrl = "")
    whenInvoke(service.details(1, "en")).thenReturn(Single.just(detailsData))
    assertThat(underTest.loadDetails(1, "en").blockingGet()).isEqualTo(details)
  }

  @Test(expected = RuntimeException::class)
  fun `fails when error occurred while load movie details`() {
    val err = RuntimeException("failed")
    whenInvoke(service.details(1, "en")).thenReturn(Single.error(err))
    underTest.loadDetails(1, "en").blockingGet()
  }
}
