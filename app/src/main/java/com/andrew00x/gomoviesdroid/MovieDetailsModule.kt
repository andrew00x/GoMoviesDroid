package com.andrew00x.gomoviesdroid

import com.andrew00x.gomoviesdroid.catalog.CatalogModel
import com.andrew00x.gomoviesdroid.catalog.Movie
import com.andrew00x.gomoviesdroid.catalog.MovieDetailsPresenter
import com.andrew00x.gomoviesdroid.config.ConfigurationModel
import dagger.Module
import dagger.Provides

@MovieDetailsScope
@Module
class MovieDetailsModule(private val movie: Movie) {
  @MovieDetailsScope
  @Provides
  fun movieDetailsPresenter(model: CatalogModel, config: ConfigurationModel, errorHandler: ErrorHandler): MovieDetailsPresenter =
      MovieDetailsPresenter(model, config, errorHandler, movie)
}