package com.andrew00x.gomoviesdroid.playlist

import com.andrew00x.gomoviesdroid.ErrorHandler
import com.andrew00x.gomoviesdroid.catalog.CatalogModel
import com.andrew00x.gomoviesdroid.catalog.CatalogPresenter
import com.andrew00x.gomoviesdroid.catalog.CatalogView
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class PlaylistAppendPresenter @Inject constructor(
    private val catalog: CatalogModel,
    private val errorHandler: ErrorHandler,
    private val playlistModel: PlaylistModel
) {

  private val subscriptions: CompositeDisposable = CompositeDisposable()
  private var catalogPresenter: CatalogPresenter? = null
  lateinit var playlist: Playlist

  fun attach(view: CatalogView) {
    catalogPresenter = CatalogPresenter(catalog, errorHandler).also { it.attach(view) }
    subscriptions.add(view.clickMovie().subscribe { movie ->
      try {
        playlistModel.addItem(playlist, movie)
        view.remove(movie)
      } catch (e: Exception) {
        errorHandler.handleError(view, e)
      }
    })
  }

  fun detach() {
    subscriptions.clear()
    catalogPresenter?.detach()
  }
}
