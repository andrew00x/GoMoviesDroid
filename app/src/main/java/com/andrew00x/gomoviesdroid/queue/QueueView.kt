package com.andrew00x.gomoviesdroid.queue

import com.andrew00x.gomoviesdroid.ui.BaseView
import io.reactivex.Observable

interface QueueView : BaseView {
  fun setQueue(queue: List<QueueItem>)
  fun clickOnItem(): Observable<QueueItem>
  fun clickOnDelete(): Observable<QueueItem>
  fun clickOnRefresh(): Observable<Any>
}
