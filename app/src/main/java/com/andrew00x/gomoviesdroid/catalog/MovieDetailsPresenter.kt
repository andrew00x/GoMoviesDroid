package com.andrew00x.gomoviesdroid.catalog

import com.andrew00x.gomoviesdroid.ResultObserver
import com.andrew00x.gomoviesdroid.ErrorHandler
import com.andrew00x.gomoviesdroid.config.ConfigurationModel
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers.io
import javax.inject.Inject

class MovieDetailsPresenter @Inject constructor(
    private val catalog: CatalogModel,
    private val config: ConfigurationModel,
    private val errorHandler: ErrorHandler,
    private val movie: Movie) {

  private val subscriptions: CompositeDisposable = CompositeDisposable()

  fun attach(view: MovieDetailsView) {
    try {
      view.setLanguages(config.get().detailLangs)
    } catch(e: Exception) {
      errorHandler.handleError(view, e)
    }
    subscriptions.add(view.changeLanguage().subscribe { lang ->
      view.showLoader()
      catalog.loadDetails(movie.id, lang).subscribeOn(io()).observeOn(mainThread())
          .subscribeWith(ResultObserver<MovieDetails>(
              { data -> view.setDetails(data, lang) },
              { err -> errorHandler.handleError(view, err) },
              { view.hideLoader() }
          ))
    })
  }

  fun detach() {
    subscriptions.clear()
  }
}
