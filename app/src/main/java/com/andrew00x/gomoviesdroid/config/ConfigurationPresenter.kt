package com.andrew00x.gomoviesdroid.config

import com.andrew00x.gomoviesdroid.BasePresenter
import com.andrew00x.gomoviesdroid.HttpResponseObserver
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers.io

class ConfigurationPresenter(private val model: ConfigurationModel, private val view: ConfigurationView) : BasePresenter {
    private var config: Configuration? = null
    private val subscriptions = CompositeDisposable()

    override fun start() {
        subscriptions.add(model.changeServerUrl().subscribe { value -> config?.serverUrl = value })
        subscriptions.add(model.toggleSavePlaybackOnStop().subscribe { value -> config?.savePlaybackOnStop = value })
        subscriptions.add(
                model.clickOnSaveConfiguration().subscribe {
                    (if (config == null) configInitError() else model.saveConfiguration(config!!))
                            .subscribeOn(io()).observeOn(mainThread())
                            .subscribeWith(object : HttpResponseObserver<Any>(view) {
                                override fun onSuccess(body: Any) {
                                    view.onConfigurationSaved()
                                }
                            })
                })
        refresh()
    }

    private fun configInitError(): Observable<RuntimeException> {
        return Observable.error<RuntimeException>(RuntimeException("Unable save configuration, error occurred when on initialization"))
    }

    override fun stop() {
        subscriptions.dispose()
    }

    override fun refresh() {
        model.readConfiguration().subscribeOn(io()).observeOn(mainThread())
                .subscribeWith(object : HttpResponseObserver<Configuration>(view) {
                    override fun onSuccess(body: Configuration) {
                        this@ConfigurationPresenter.config = body
                        view.showConfiguration(body)
                    }
                })
    }
}