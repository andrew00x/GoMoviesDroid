package com.andrew00x.gomoviesdroid.player

import com.andrew00x.gomoviesdroid.ResultObserver
import com.andrew00x.gomoviesdroid.ErrorHandler
import com.andrew00x.gomoviesdroid.PlayerStatus
import com.andrew00x.gomoviesdroid.catalog.CatalogModel
import com.andrew00x.gomoviesdroid.catalog.CatalogPresenter
import com.andrew00x.gomoviesdroid.catalog.CatalogView
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers.io
import javax.inject.Inject

class PlayerCatalogPresenter @Inject constructor(
    private val catalog: CatalogModel,
    private val errorHandler: ErrorHandler,
    private val player: PlayerModel
) {

  private val subscriptions: CompositeDisposable = CompositeDisposable()
  private var catalogPresenter: CatalogPresenter? = null

  fun attach(view: CatalogView) {
    catalogPresenter = CatalogPresenter(catalog, errorHandler)
    catalogPresenter?.attach(view)
    subscriptions.add(view.clickMovie().subscribe { movie ->
      view.showLoader()
      player.play(movie.file).subscribeOn(io()).observeOn(mainThread())
          .subscribeWith(ResultObserver<PlayerStatus>(
              whenError = { err -> errorHandler.handleError(view, err) },
              always = { view.hideLoader() }
          ))
    })
  }

  fun detach() {
    subscriptions.clear()
    catalogPresenter?.detach()
  }
}
