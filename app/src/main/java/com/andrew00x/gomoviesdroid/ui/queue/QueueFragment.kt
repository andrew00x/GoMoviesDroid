package com.andrew00x.gomoviesdroid.ui.queue

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ListView
import com.andrew00x.gomoviesdroid.GomoviesApplication
import com.andrew00x.gomoviesdroid.R
import com.andrew00x.gomoviesdroid.queue.QueueItem
import com.andrew00x.gomoviesdroid.queue.QueuePresenter
import com.andrew00x.gomoviesdroid.queue.QueueView
import com.andrew00x.gomoviesdroid.ui.BaseFragment
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxAdapterView
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class QueueFragment : BaseFragment(), QueueView {
  companion object {
    fun newInstance() = QueueFragment()
  }

  private val onDelete: PublishSubject<QueueItem> = PublishSubject.create()
  private lateinit var queueView: ListView
  private lateinit var queue: QueueAdapter
  private lateinit var refreshButton: ImageButton

  @Inject lateinit var presenter: QueuePresenter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    (activity!!.application as GomoviesApplication).component.inject(this)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    val view = inflater.inflate(R.layout.fragment_queue, container, false)!!
    queueView = view.findViewById(R.id.queue_movie_list)
    queueView.emptyView = view.findViewById(R.id.queue_empty_message)
    refreshButton = view.findViewById(R.id.queue_refresh)
    queue = QueueAdapter(activity!!, mutableListOf(), onDelete)
    queueView.adapter = queue
    return view
  }

  override fun onResume() {
    super.onResume()
    presenter.attach(this)
  }

  override fun onPause() {
    presenter.detach()
    super.onPause()
  }

  override fun setQueue(queue: List<QueueItem>) {
    this.queue.clear()
    this.queue.addAll(queue)
  }

  override fun clickOnItem(): Observable<QueueItem> = RxAdapterView.itemClicks(queueView).map { queue.getItem(it) }

  override fun clickOnDelete(): Observable<QueueItem> = onDelete

  override fun clickOnRefresh(): Observable<Any> = RxView.clicks(refreshButton)

  override fun refresh() {
    presenter.refresh(this)
  }
}
