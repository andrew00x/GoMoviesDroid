package com.andrew00x.gomoviesdroid.catalog

import com.andrew00x.gomoviesdroid.BasePresenter
import com.andrew00x.gomoviesdroid.HttpResponseObserver
import com.andrew00x.gomoviesdroid.Movie
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers.io
import java.util.concurrent.TimeUnit.MILLISECONDS

class CatalogPresenter(private val model: CatalogModel, private val view: CatalogView) : BasePresenter {
    private var subscriptions: CompositeDisposable = CompositeDisposable()

    override fun start() {
        subscriptions.add(model.clickMovie().subscribe { movie ->
            model.playMovie(movie).subscribeOn(io()).observeOn(mainThread())
                    .subscribeWith(object : HttpResponseObserver<Any>(view) {
                        override fun onSuccess(body: Any) {
                            view.onPlaybackStarted()
                        }
                    })
        })
        subscriptions.add(model.changeSearchField().debounce(300, MILLISECONDS)
                .subscribe { title ->
                    (if (title.isEmpty()) model.listMovies() else model.searchMovies(title))
                            .subscribeOn(io()).observeOn(mainThread())
                            .subscribeWith(object : HttpResponseObserver<List<Movie>>(view) {
                                override fun onSuccess(body: List<Movie>) {
                                    view.showMovies(body)
                                }
                            })
                })
        subscriptions.add(model.changeSearchField().subscribe({ title -> if (title.isEmpty()) view.hideClearSearchButton() else view.showClearSearchButton() }))
        subscriptions.add(model.clickClearSearchButton().subscribe({ view.clearSearchField() }))
        model.listMovies().subscribeOn(io()).observeOn(mainThread())
                .subscribeWith(object : HttpResponseObserver<List<Movie>>(view) {
                    override fun onSuccess(body: List<Movie>) {
                        view.showMovies(body)
                    }
                })
    }

    override fun stop() {
        subscriptions.dispose()
    }

    override fun refresh() {}
}