package com.andrew00x.gomoviesdroid.config

import com.andrew00x.gomoviesdroid.ErrorHandler
import io.reactivex.disposables.CompositeDisposable
import java.lang.Exception

class ConfigurationPresenter(
    private val model: ConfigurationModel,
    private val listener: ConfigurationListener,
    private val errorHandler: ErrorHandler) {
  private val subscriptions = CompositeDisposable()

  fun attach(view: ConfigurationView) {
    try {
      val config = model.get()
      view.setServer(config.server)
      view.setPort(config.port)
      view.setDetailLanguages(config.detailLangs)
    } catch (e: Exception) {
      errorHandler.handleError(view, e)
    }
    subscriptions.add(view.clickOnSave().subscribe {
      try {
        val config = Configuration(view.getServer(), view.getPort(), view.getDetailLanguages())
        model.save(config)
        listener.afterSave(config)
      } catch (e: Exception) {
        errorHandler.handleError(view, e)
      }
    })
  }

  fun detach() {
    subscriptions.clear()
  }
}
