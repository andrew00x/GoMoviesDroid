package com.andrew00x.gomoviesdroid.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import com.andrew00x.gomoviesdroid.*
import com.andrew00x.gomoviesdroid.catalog.CatalogEventSource
import com.andrew00x.gomoviesdroid.catalog.CatalogPresenter
import com.andrew00x.gomoviesdroid.catalog.CatalogView
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import javax.inject.Inject

class CatalogFragment : CatalogView, CatalogEventSource, BaseFragment() {
  private lateinit var searchField: EditText
  private lateinit var clearSearchButton: ImageButton
  private lateinit var moviesListView: ListView
  private lateinit var moviesList: ArrayAdapter<Movie>

  @Inject lateinit var presenter: CatalogPresenter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    (activity.application as GomoviesApplication).component.injectInto(this)
  }

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
    val view = inflater?.inflate(R.layout.fragment_catalog, container, false)!!
    initView(view)
    presenter.attach(this, this)
    return view
  }

  private fun initView(view: View) {
    moviesListView = view.findViewById(R.id.movies_list)
    moviesList = MoviesList(activity, 0, mutableListOf())
    moviesListView.adapter = moviesList
    searchField = view.findViewById(R.id.search_field)
    clearSearchButton = view.findViewById(R.id.clear_search_field_button)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    presenter.detach()
  }

  override fun refresh() {
    presenter.refresh(this)
  }

  override fun showMovies(movies: List<Movie>) {
    moviesList.clear()
    moviesList.addAll(movies)
    moviesList.notifyDataSetChanged()
  }

  override fun showClearSearchButton() {
    clearSearchButton.visibility = View.VISIBLE
  }

  override fun hideClearSearchButton() {
    clearSearchButton.visibility = View.INVISIBLE
  }

  override fun clearSearchField() {
    searchField.setText("")
  }

  override fun onPlaybackStarted() {
    if (activity is PlaybackListener) (activity as PlaybackListener).onPlaybackStarted()
  }

  override fun onPlaybackStopped() {
    if (activity is PlaybackListener) (activity as PlaybackListener).onPlaybackStopped()
  }

  override fun changeSearchField(): Observable<String> {
    return RxTextView.textChanges(searchField).map { title -> title.toString() }
  }

  override fun clickClearSearchField(): Observable<Any> {
    return RxView.clicks(clearSearchButton)
  }

  override fun clickMovie(): Observable<Movie> {
    return Observable.create<Movie> { emitter ->
      emitter.setCancellable { moviesListView.onItemClickListener = null }
      moviesListView.setOnItemClickListener { parent, _, position, _ ->
        emitter.onNext(parent.adapter.getItem(position) as Movie)
      }
    }
  }
}
