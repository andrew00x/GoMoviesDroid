package com.andrew00x.gomoviesdroid.queue

import com.andrew00x.gomoviesdroid.ResultObserver
import com.andrew00x.gomoviesdroid.ErrorHandler
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers.io
import javax.inject.Inject

class QueuePresenter @Inject constructor(
    private val queue: QueueModel,
    private val errorHandler: ErrorHandler) {
  private val subscriptions: CompositeDisposable = CompositeDisposable()

  fun attach(view: QueueView) {
    subscriptions.add(view.clickOnDelete().subscribe { item ->
      view.showLoader()
      queue.remove(item).subscribeOn(io()).observeOn(mainThread())
          .subscribeWith(ResultObserver(
              { data -> view.setQueue(data) },
              { err -> errorHandler.handleError(view, err) },
              { view.hideLoader() }
          ))
    })
    subscriptions.add(view.clickOnItem().subscribe { item ->
      view.showLoader()
      queue.shift(item).subscribeOn(io()).observeOn(mainThread())
          .subscribeWith(ResultObserver(
              { data -> view.setQueue(data) },
              { err -> errorHandler.handleError(view, err) },
              { view.hideLoader() }
          ))
    })
    subscriptions.add(view.clickOnRefresh().subscribe {
      refresh(view)
    })
    subscriptions.add(refresh(view))
  }

  fun refresh(view: QueueView): Disposable {
    view.showLoader()
    return queue.getAll().subscribeOn(io()).observeOn(mainThread())
        .subscribeWith(ResultObserver(
            { data -> view.setQueue(data) },
            { err -> errorHandler.handleError(view, err) },
            { view.hideLoader() }
        ))
  }

  fun detach() {
    subscriptions.clear()
  }
}
