package com.andrew00x.gomoviesdroid.queue

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.andrew00x.gomoviesdroid.GomoviesService
import io.reactivex.Single
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.Mockito.`when` as whenInvoke

@RunWith(MockitoJUnitRunner::class)
class QueueModelTest {
  @Mock private lateinit var service: GomoviesService
  @InjectMocks private lateinit var underTest: QueueModel

  @Test fun `load movies in queue`() {
    val queue = listOf("/movies/gladiator.mkv", "/movies/brave heart.mkv")
    whenInvoke(service.queue()).thenReturn(Single.just(queue))

    assertThat(underTest.getAll().blockingGet()).isEqualTo(listOf(
        QueueItem("/movies/gladiator.mkv", 0),
        QueueItem("/movies/brave heart.mkv", 1)
    ))
  }

  @Test fun `enqueue movies`() {
    val req = listOf("/movies/gladiator.mkv", "/movies/brave heart.mkv")
    val queue = listOf("/movies/brave heart.mkv")
    whenInvoke(service.enqueue(req)).thenReturn(Single.just(queue))

    assertThat(underTest.enqueue(req).blockingGet()).isEqualTo(listOf(QueueItem("/movies/brave heart.mkv", 0)))
  }

  @Test fun `remove movie from queue`() {
    val queue = listOf("/movies/gladiator.mkv")
    whenInvoke(service.dequeue(1)).thenReturn(Single.just(queue))

    underTest.remove(QueueItem("/movies/brave heart.mkv", 1)).blockingGet()

    assertThat(underTest.remove(QueueItem("/movies/brave heart.mkv", 1)).blockingGet())
        .isEqualTo(listOf(QueueItem("/movies/gladiator.mkv", 0)))
  }

  @Test fun `clears queue`() {
    underTest.clear()
    verify(service).clearQueue()
  }
}
