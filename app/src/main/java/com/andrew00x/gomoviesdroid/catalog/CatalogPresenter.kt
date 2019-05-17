package com.andrew00x.gomoviesdroid.catalog

import android.annotation.SuppressLint
import com.andrew00x.gomoviesdroid.*
import com.andrew00x.gomoviesdroid.player.PlayerModel
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers.io
import java.util.concurrent.TimeUnit.MILLISECONDS
import javax.inject.Inject

class CatalogPresenter @Inject constructor(
    private val catalog: CatalogModel,
    private val player: PlayerModel,
    private val errorHandler: ErrorHandler) {
  private var subscriptions: CompositeDisposable = CompositeDisposable()

  fun attach(view: CatalogView, events: CatalogEventSource) {
    subscriptions.add(events.clickMovie().subscribe { movie ->
      mainThread().scheduleDirect { view.showLoader() }
      player.play(movie.file).subscribeOn(io()).observeOn(mainThread())
          .subscribeWith(DefaultObserver<PlayerStatus>(
              { view.hideLoader(); view.onPlaybackStarted() },
              { err -> view.hideLoader(); errorHandler.handleError(view, err) }
          ))
    })
    subscriptions.add(events.changeSearchField().debounce(300, MILLISECONDS).subscribe { title ->
      mainThread().scheduleDirect { view.showLoader() }
      (if (title.isBlank()) catalog.list() else catalog.search(title.trim())).subscribeOn(io()).observeOn(mainThread())
          .subscribeWith(DefaultObserver<List<Movie>>(
              { movies -> view.hideLoader(); view.showMovies(movies) },
              { err -> view.hideLoader(); errorHandler.handleError(view, err) }
          ))
    })
    subscriptions.add(events.changeSearchField().subscribe { title ->
      if (title.isBlank()) view.hideClearSearchButton() else view.showClearSearchButton()
    })
    subscriptions.add(events.clickClearSearchField().subscribe { view.clearSearchField() })

    refresh(view)
  }

  @SuppressLint("CheckResult")
  fun refresh(view: CatalogView) {
    view.showLoader()
    catalog.list().subscribeOn(io()).observeOn(mainThread())
        .subscribeWith(DefaultObserver<List<Movie>>(
            { movies -> view.hideLoader(); view.showMovies(movies) },
            { err -> view.hideLoader(); errorHandler.handleError(view, err) }
        ))
  }

  fun detach() {
    subscriptions.dispose()
  }
}