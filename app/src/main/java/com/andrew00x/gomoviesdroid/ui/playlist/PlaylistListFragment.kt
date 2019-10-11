package com.andrew00x.gomoviesdroid.ui.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ListView
import com.andrew00x.gomoviesdroid.GomoviesApplication
import com.andrew00x.gomoviesdroid.R
import com.andrew00x.gomoviesdroid.playlist.*
import com.andrew00x.gomoviesdroid.ui.BaseFragment
import com.andrew00x.gomoviesdroid.ui.MainActivity
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxAdapterView
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class PlaylistListFragment : BaseFragment(), PlaylistListView {
  companion object {
    fun newInstance(): PlaylistListFragment = PlaylistListFragment()
  }

  private val subscriptions: CompositeDisposable = CompositeDisposable()
  private val onEdit: PublishSubject<Playlist> = PublishSubject.create()
  private val onSave: PublishSubject<Playlist> = PublishSubject.create()
  private val onDelete: PublishSubject<Playlist> = PublishSubject.create()
  private lateinit var playlistListView: ListView
  private lateinit var createPlaylistButton: ImageButton
  private lateinit var playlistList: PlaylistListAdapter

  @Inject lateinit var listPresenter: PlaylistListPresenter
  @Inject lateinit var editNamePresenter: PlaylistNamePresenter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    (activity!!.application as GomoviesApplication).component.inject(this)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    val view = inflater.inflate(R.layout.fragment_playlist_list, container, false)!!
    playlistListView = view.findViewById(R.id.playlist_list)
    playlistListView.emptyView = view.findViewById(R.id.playlist_list_empty_message)
    createPlaylistButton = view.findViewById(R.id.playlist_create)
    playlistList = PlaylistListAdapter(activity as MainActivity, mutableListOf(), editNamePresenter, onEdit, onSave, onDelete)
    playlistListView.adapter = playlistList
    return view
  }

  override fun onResume() {
    super.onResume()
    listPresenter.attach(this)
    subscriptions.add(onSave.subscribe { stopEditPlaylist() })
  }

  override fun onPause() {
    subscriptions.clear()
    listPresenter.detach()
    super.onPause()
  }

  override fun clickOnCreate(): Observable<Any> {
    return RxView.clicks(createPlaylistButton)
  }

  override fun setPlaylistList(playlistList: List<Playlist>) {
    this.playlistList.clear()
    this.playlistList.addAll(playlistList)
  }

  override fun clickOnPlaylist(): Observable<Playlist> {
    return RxAdapterView.itemClicks(playlistListView).map { playlistList.getItem(it) }
  }

  override fun longClickOnPlaylist(): Observable<Playlist> {
    return RxAdapterView.itemLongClicks(playlistListView).map { playlistList.getItem(it) }
  }

  override fun showPlaylist(playlist: Playlist) {
    val fr = PlaylistFragment.newInstance(playlist)
    fr.setTargetFragment(this, 0)
    fr.show(fragmentManager, PlaylistFragment.TAG)
  }

  override fun editPlaylist(playlist: Playlist) {
    playlistList.updating = playlistList.getPosition(playlist)
    playlistList.notifyDataSetChanged()
  }

  private fun stopEditPlaylist() {
    playlistList.updating = -1
    playlistList.notifyDataSetChanged()
  }

  override fun clickOnEdit(): Observable<Playlist> {
    return onEdit
  }

  override fun clickOnDelete(): Observable<Playlist> {
    return onDelete
  }
}
