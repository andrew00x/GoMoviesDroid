package com.andrew00x.gomoviesdroid

import io.reactivex.CompletableObserver
import io.reactivex.disposables.Disposable

class EmptyResultObserver(
    private val whenSuccess: () -> Unit = {},
    private val whenError: (Throwable) -> Unit = {},
    private val always: () -> Unit = {}
) : CompletableObserver, Disposable {
  private var disposable: Disposable? = null
  override fun onSubscribe(d: Disposable) {
    disposable = d
  }

  override fun onComplete() {
    dispose()
    always()
    whenSuccess()
  }

  override fun onError(err: Throwable) {
    dispose()
    always()
    whenError(err)
  }

  override fun dispose() {
    disposable?.dispose()
  }

  override fun isDisposed(): Boolean {
    return disposable?.isDisposed ?: false
  }
}