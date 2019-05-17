package com.andrew00x.gomoviesdroid.catalog

import com.andrew00x.gomoviesdroid.BaseView
import com.andrew00x.gomoviesdroid.Movie
import com.andrew00x.gomoviesdroid.PlaybackListener

interface CatalogView : BaseView, PlaybackListener {
  fun showClearSearchButton()
  fun hideClearSearchButton()
  fun clearSearchField()
  fun showMovies(movies: List<Movie>)
}