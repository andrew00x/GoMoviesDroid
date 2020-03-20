package com.andrew00x.gomoviesdroid

import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable

class ResultObserver<T>(
    private val whenSuccess: (T) -> Unit = {},
    private val whenError: (Throwable) -> Unit = {},
    private val always: () -> Unit = {}
) : SingleObserver<T>, Disposable {
  private var disposable: Disposable? = null
  override fun onSubscribe(d: Disposable) {
    disposable = d
  }

  override fun onSuccess(t: T) {
    dispose()
    always()
    whenSuccess(t)
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
