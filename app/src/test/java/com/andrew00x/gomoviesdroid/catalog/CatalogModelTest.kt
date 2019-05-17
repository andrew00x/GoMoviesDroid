package com.andrew00x.gomoviesdroid.catalog

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.andrew00x.gomoviesdroid.GomoviesService
import com.andrew00x.gomoviesdroid.Movie
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
  @InjectMocks lateinit var model: CatalogModel

  @Test
  fun `search movies`() {
    val movies = listOf(
        Movie(2, "brave heart.mkv", "/movies/brave heart.mkv", "movies_1", true)
    )
    whenInvoke(service.search("brave")).thenReturn(Single.just(movies))
    assertThat(model.search("brave").blockingGet()).isEqualTo(movies)
  }

  @Test
  fun `list movies`() {
    val movies = listOf(
        Movie(1, "gladiator.mkv", "/movies/gladiator.mkv", "movies_1", true),
        Movie(2, "brave heart.mkv", "/movies/brave heart.mkv", "movies_1", true)
    )
    whenInvoke(service.list()).thenReturn(Single.just(movies))
    assertThat(model.list().blockingGet()).isEqualTo(movies)
  }
}