package com.andrew00x.gomoviesdroid.queue

import com.andrew00x.gomoviesdroid.GomoviesService
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

data class QueueItem(
    val moviePath: String,
    val position: Int
)

@Singleton
class QueueModel @Inject constructor(private val service: GomoviesService) {
  fun getAll(): Single<List<QueueItem>> =
      service.queue().map { it.mapIndexed { index, value -> QueueItem(value, index) } }

  fun enqueue(paths: List<String>, clear: Boolean = false): Single<List<QueueItem>> {
    return (if (clear) {
      clear().flatMap { service.enqueue(paths) }
    } else service.enqueue(paths)).map { it.mapIndexed { index, value -> QueueItem(value, index) } }
  }

  fun remove(item: QueueItem): Single<List<QueueItem>> =
      service.dequeue(item.position).map { it.mapIndexed { index, value -> QueueItem(value, index) } }

  fun clear(): Single<Unit> = service.clearQueue()

  fun shift(item: QueueItem): Single<List<QueueItem>> =
      service.shiftQueue(item.position).map { it.mapIndexed { index, value -> QueueItem(value, index) } }
}

