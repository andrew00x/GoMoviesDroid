package com.andrew00x.gomoviesdroid

import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable

class DefaultObserver<T>(
    private val whenSuccess: (T) -> Unit,
    private val whenError: (Throwable) -> Unit
) : SingleObserver<T>, Disposable {
  private var disposable: Disposable? = null
  override fun onSubscribe(d: Disposable) {
    disposable = d
  }

  override fun onSuccess(t: T) {
    dispose()
    whenSuccess(t)
  }

  override fun onError(err: Throwable) {
    dispose()
    whenError(err)
  }

  override fun dispose() {
    disposable?.dispose()
  }

  override fun isDisposed(): Boolean {
    return disposable?.isDisposed ?: false
  }
}
