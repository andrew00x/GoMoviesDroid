package com.andrew00x.gomoviesdroid.ui.torrent

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ListView
import com.andrew00x.gomoviesdroid.GomoviesApplication
import com.andrew00x.gomoviesdroid.R
import com.andrew00x.gomoviesdroid.TorrentModule
import com.andrew00x.gomoviesdroid.torrent.TorrentDownloadItem
import com.andrew00x.gomoviesdroid.torrent.TorrentPresenter
import com.andrew00x.gomoviesdroid.torrent.TorrentView
import com.andrew00x.gomoviesdroid.ui.BaseFragment
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxAdapterView
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class TorrentFragment : BaseFragment(), TorrentView {
  companion object {
    private const val TORRENT_MIME_TYPE = "application/x-bittorrent"

    fun newInstance() = TorrentFragment()
  }

  private val onDelete: PublishSubject<TorrentDownloadItem> = PublishSubject.create()
  private val onSelectTorrent: BehaviorSubject<Uri> = BehaviorSubject.create()
  private lateinit var torrentsView: ListView
  private lateinit var torrents: TorrentAdapter
  private lateinit var addButton: ImageButton
  private lateinit var refreshButton: ImageButton
  @Inject lateinit var presenter: TorrentPresenter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    (activity!!.application as GomoviesApplication).component.plus(TorrentModule()).inject(this)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    val view = inflater.inflate(R.layout.fragment_torrent, container, false)
    torrentsView = view.findViewById(R.id.torrent_list)
    torrentsView.emptyView = view.findViewById(R.id.torrent_list_empty_message)
    addButton = view.findViewById(R.id.torrent_add)
    refreshButton = view.findViewById(R.id.torrent_refresh)
    torrents = TorrentAdapter(activity!!, mutableListOf(), onDelete)
    torrentsView.adapter = torrents
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

  override fun setTorrents(torrents: List<TorrentDownloadItem>) {
    this.torrents.clear()
    this.torrents.addAll(torrents)
  }

  override fun selectTorrentFile() {
    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
      addCategory(Intent.CATEGORY_OPENABLE)
      type = TORRENT_MIME_TYPE
    }
    startActivityForResult(intent, 1)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
      data?.data?.also {
        onSelectTorrent.onNext(it)
      }
    }
  }

  override fun torrentFileSelected(): Observable<Uri> = onSelectTorrent

  override fun updateTorrent(torrent: TorrentDownloadItem) {
    torrents.getItem(torrent.position)!!.data = torrent.data
    torrents.notifyDataSetChanged()
  }

  override fun clickOnItem(): Observable<TorrentDownloadItem> = RxAdapterView.itemClicks(torrentsView).map { torrents.getItem(it) }

  override fun clickOnDelete(): Observable<TorrentDownloadItem> = onDelete

  override fun clickOnAddTorrent(): Observable<Any> = RxView.clicks(addButton)

  override fun clickOnRefresh(): Observable<Any> = RxView.clicks(refreshButton)
}
