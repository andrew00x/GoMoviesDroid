package com.andrew00x.gomoviesdroid.catalog

import com.andrew00x.gomoviesdroid.ui.BaseView
import io.reactivex.Observable

interface CatalogView : BaseView {
  fun setMovies(movies: List<Movie>)
  fun changeSearchField(): Observable<String>
  fun clickClearSearchField(): Observable<Any>
  fun getSearchField(): String
  fun clickMovie(): Observable<Movie>
  fun longClickMovie(): Observable<Movie>
  fun clearSearchField()
  fun showClearSearch()
  fun hideClearSearch()
  fun remove(movie: Movie)
  fun showDetailsFor(movie: Movie)
}
