package com.andrew00x.gomoviesdroid.ui.playlist

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView
import com.andrew00x.gomoviesdroid.GomoviesApplication
import com.andrew00x.gomoviesdroid.R
import com.andrew00x.gomoviesdroid.playlist.Playlist
import com.andrew00x.gomoviesdroid.playlist.PlaylistPresenter
import com.andrew00x.gomoviesdroid.playlist.PlaylistView
import com.andrew00x.gomoviesdroid.playlist.PlaylistItem
import com.andrew00x.gomoviesdroid.ui.BaseDialogFragment
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class PlaylistFragment : BaseDialogFragment(), PlaylistView {
  companion object {
    const val ARG = "Playlist"
    const val TAG = "PlaylistContent"

    fun newInstance(playlist: Playlist): PlaylistFragment {
      val args = Bundle()
      args.putParcelable(ARG, playlist)
      val fr = PlaylistFragment()
      fr.arguments = args
      return fr
    }
  }

  private val onDelete: PublishSubject<PlaylistItem> = PublishSubject.create()
  @Inject lateinit var presenter: PlaylistPresenter
  private lateinit var playlist: Playlist
  private lateinit var addItemsButton: ImageButton
  private lateinit var playlistContentData: ArrayAdapter<PlaylistItem>

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    playlist = arguments!!.getParcelable(ARG)!!
    (activity!!.application as GomoviesApplication).component.inject(this)
    inFullScreen()
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.fragment_playlist_content, null)!!
    val emptyMessage = view.findViewById<View>(R.id.playlist_empty_message)
    val listView = view.findViewById<ListView>(R.id.playlist)
    listView.emptyView = emptyMessage
    addItemsButton = view.findViewById(R.id.playlist_add_items)

    playlistContentData = PlaylistAdapter(activity!!, mutableListOf(), onDelete)
    listView.adapter = playlistContentData
    presenter.playlist = playlist
    return view
  }

  override fun onPause() {
    presenter.detach()
    super.onPause()
  }

  override fun onResume() {
    super.onResume()
    presenter.attach(this)
  }

  override fun setPlaylist(playlist: List<PlaylistItem>) {
    playlistContentData.clear()
    playlistContentData.addAll(playlist)
  }

  override fun clickOnDelete(): Observable<PlaylistItem> {
    return onDelete
  }

  override fun clickOnAddItems(): Observable<Any> {
    return RxView.clicks(addItemsButton)
  }

  override fun startAddItems() {
    val fr = PlaylistAppendFragment.newInstance(playlist)
    fr.setTargetFragment(this, 0)
    fr.show(fragmentManager, PlaylistAppendFragment.TAG)
    fr.onDismissListener = DialogInterface.OnDismissListener {
      presenter.refresh(this)
    }
  }
}
