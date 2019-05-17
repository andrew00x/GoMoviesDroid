package com.andrew00x.gomoviesdroid.config

import android.annotation.SuppressLint
import com.andrew00x.gomoviesdroid.ErrorHandler
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers.io
import javax.inject.Inject

class ConfigurationPresenter @Inject constructor(
    private val configurationModel: ConfigurationModel,
    private val errorHandler: ErrorHandler) {
  private val subscriptions = CompositeDisposable()
  private var configuration: Configuration? = null

  fun attach(view: ConfigurationView, events: ConfigurationEventSource) {
    subscriptions.add(events.changeServer().subscribe { value -> configuration?.server = value })
    subscriptions.add(events.changePort().subscribe { value -> configuration?.port = if (value.isBlank()) 80 else value.toInt() })
    subscriptions.add(events.clickOnSaveConfiguration().subscribe {
      configurationModel.save(configuration!!).subscribeOn(io()).observeOn(mainThread())
          .subscribe(
              { view.onConfigurationSaved() },
              { err -> errorHandler.handleError(view, err) }
          )
    })
    refresh(view)
  }

  fun detach() {
    subscriptions.dispose()
    configuration = null
  }

  @SuppressLint("CheckResult")
  fun refresh(view: ConfigurationView) {
    configurationModel.read().subscribeOn(io()).observeOn(mainThread())
        .subscribe({ config ->
          ConfigurationPresenter@ this.configuration = config
          view.showServer(config.server)
          view.showPort(config.port)
        }, { err ->
          errorHandler.handleError(view, err)
        })
  }
}