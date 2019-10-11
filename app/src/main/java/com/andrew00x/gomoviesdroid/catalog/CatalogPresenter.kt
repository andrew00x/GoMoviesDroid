package com.andrew00x.gomoviesdroid.catalog

import com.andrew00x.gomoviesdroid.*
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers.io
import java.util.concurrent.TimeUnit.MILLISECONDS

class CatalogPresenter(
    private val catalog: CatalogModel,
    private val errorHandler: ErrorHandler) {
  private val subscriptions: CompositeDisposable = CompositeDisposable()

  fun attach(view: CatalogView) {
    // skip(1) is workaround since RxTextView.textChanges generates one extra event when attach listener
    subscriptions.add(view.changeSearchField().skip(1).debounce(300, MILLISECONDS).subscribe { title ->
      mainThread().scheduleDirect { view.showLoader() }
      catalog.load(title).subscribeOn(io()).observeOn(mainThread())
          .subscribeWith(DefaultObserver<List<Movie>>(
              { movies -> view.setMovies(movies) },
              { err -> errorHandler.handleError(view, err) },
              { view.hideLoader() }
          ))
    })
    subscriptions.add(view.changeSearchField().subscribe { title ->
      if (title.isBlank()) view.hideClearSearch() else view.showClearSearch()
    })
    subscriptions.add(view.clickClearSearchField().subscribe {
      view.clearSearchField()
    })
    subscriptions.add(view.longClickMovie().subscribe { movie ->
      view.showDetailsFor(movie)
    })
    subscriptions.add(catalog.load(view.getSearchField()).subscribeOn(io()).observeOn(mainThread())
        .doOnSubscribe { view.showLoader() }
        .subscribeWith(DefaultObserver<List<Movie>>(
            { movies -> view.setMovies(movies) },
            { err -> errorHandler.handleError(view, err) },
            { view.hideLoader() }
        ))
    )
  }

  fun detach() {
    subscriptions.clear()
  }
}
