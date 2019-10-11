package com.andrew00x.gomoviesdroid.ui.playlist

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.andrew00x.gomoviesdroid.GomoviesApplication
import com.andrew00x.gomoviesdroid.R
import com.andrew00x.gomoviesdroid.playlist.Playlist
import com.andrew00x.gomoviesdroid.playlist.PlaylistAppendPresenter
import com.andrew00x.gomoviesdroid.ui.BaseDialogFragment
import com.andrew00x.gomoviesdroid.ui.catalog.CatalogViewHelper
import com.andrew00x.gomoviesdroid.ui.catalog.CatalogViewHolder
import com.andrew00x.gomoviesdroid.ui.catalog.MovieListAdapter
import javax.inject.Inject

class PlaylistAppendFragment : BaseDialogFragment() {
  companion object {
    const val ARG = "Playlist"
    const val TAG = "PlaylistAppend"

    fun newInstance(playlist: Playlist): PlaylistAppendFragment {
      val fr = PlaylistAppendFragment()
      val args = Bundle()
      args.putParcelable(ARG, playlist)
      fr.arguments = args
      return fr
    }
  }

  @Inject lateinit var presenter: PlaylistAppendPresenter
  private lateinit var playlist: Playlist
  private lateinit var catalogView: CatalogViewHelper
  var onDismissListener: DialogInterface.OnDismissListener? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    (activity!!.application as GomoviesApplication).component.inject(this)
    playlist = arguments!!.getParcelable(ARG)!!
    inFullScreen()
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    val view = inflater.inflate(R.layout.fragment_playlist_append, container, false)!!
    val components = CatalogViewHolder(view)
    components.movieList.adapter = MovieListAdapter(activity!!, mutableListOf())
    catalogView = CatalogViewHelper(this, components.searchField, components.clearSearchButton, components.movieList)
    presenter.playlist = playlist
    return view
  }

  override fun onResume() {
    super.onResume()
    presenter.attach(catalogView)
  }

  override fun onPause() {
    presenter.detach()
    super.onPause()
  }

  override fun onDismiss(dialog: DialogInterface?) {
    super.onDismiss(dialog)
    onDismissListener?.onDismiss(dialog)
  }
}
