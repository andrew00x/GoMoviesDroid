package com.andrew00x.gomoviesdroid.ui.catalog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.andrew00x.gomoviesdroid.GomoviesApplication
import com.andrew00x.gomoviesdroid.R
import com.andrew00x.gomoviesdroid.catalog.Movie
import com.andrew00x.gomoviesdroid.player.PlayerCatalogPresenter
import com.andrew00x.gomoviesdroid.ui.BaseFragment
import javax.inject.Inject

class CatalogFragment : BaseFragment() {
  companion object {
    fun newInstance(): CatalogFragment = CatalogFragment()
  }

  @Inject lateinit var presenter: PlayerCatalogPresenter
  private lateinit var catalogView: CatalogViewHelper

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    (activity!!.application as GomoviesApplication).component.inject(this)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    val view = inflater.inflate(R.layout.fragment_catalog, container, false)!!
    val components = CatalogViewHolder(view)
    components.movieList.adapter = MovieListAdapter(activity!!, mutableListOf())
    catalogView = CatalogViewHelper(this, components.searchField, components.clearSearchButton, components.movieList)
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

  fun openMovieDetailsView(movie: Movie) {
    val fr = MovieDetailsFragment.newInstance(movie)
    fr.setTargetFragment(this, 0)
    fr.show(fragmentManager, MovieDetailsFragment.TAG)
  }
}
