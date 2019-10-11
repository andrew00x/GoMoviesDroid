package com.andrew00x.gomoviesdroid.ui.catalog

import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import com.andrew00x.gomoviesdroid.ui.BaseView
import com.andrew00x.gomoviesdroid.catalog.CatalogView
import com.andrew00x.gomoviesdroid.catalog.Movie
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxAdapterView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable

class CatalogViewHelper(
    private val owner: BaseView,
    private val searchField: EditText,
    private val clearSearchButton: ImageButton,
    private val movieList: ListView) : CatalogView {

  private val adapter: MovieListAdapter
    get() = movieList.adapter as MovieListAdapter

  override fun setMovies(movies: List<Movie>) {
    adapter.clear()
    adapter.addAll(movies)
  }

  override fun changeSearchField(): Observable<String> =
      RxTextView.textChanges(searchField).map { title -> title.toString() }

  override fun clickClearSearchField(): Observable<Any> = RxView.clicks(clearSearchButton)

  override fun getSearchField(): String = searchField.text.toString()

  override fun clickMovie(): Observable<Movie> =
      RxAdapterView.itemClicks(movieList).map { position -> adapter.getItem(position) }

  override fun longClickMovie(): Observable<Movie> =
      RxAdapterView.itemLongClicks(movieList).map { position -> adapter.getItem(position) }

  override fun showClearSearch() {
    clearSearchButton.visibility = View.VISIBLE
  }

  override fun hideClearSearch() {
    clearSearchButton.visibility = View.INVISIBLE
  }

  override fun clearSearchField() = searchField.setText("")

  override fun showLoader() = owner.showLoader()

  override fun hideLoader() = owner.hideLoader()

  override fun showInfo(message: String) = owner.showInfo(message)

  override fun showError(message: String?) = owner.showError(message)

  override fun remove(movie: Movie) = adapter.remove(movie)

  override fun showDetailsFor(movie: Movie) {
    if (owner is CatalogFragment) owner.openMovieDetailsView(movie)
  }
}
