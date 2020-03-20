package com.andrew00x.gomoviesdroid.torrent

import com.andrew00x.gomoviesdroid.EmptyResultObserver
import com.andrew00x.gomoviesdroid.ResultObserver
import com.andrew00x.gomoviesdroid.ErrorHandler
import com.andrew00x.gomoviesdroid.file.ContentProvider
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers.io
import javax.inject.Inject

class TorrentPresenter @Inject constructor(
    private val torrents: TorrentModel,
    private val errorHandler: ErrorHandler,
    private val contentProvider: ContentProvider) {
  private val subscriptions = CompositeDisposable()

  fun attach(view: TorrentView) {
    subscriptions.add(view.clickOnDelete().subscribe { torrent ->
      view.showLoader()
      torrents.delete(torrent).subscribeOn(io()).observeOn(mainThread())
          .subscribeWith(ResultObserver(
              { data -> view.setTorrents(data) },
              { err -> errorHandler.handleError(view, err) },
              { view.hideLoader() }
          ))
    })
    subscriptions.add(view.clickOnItem().subscribe { torrent ->
      view.showLoader()
      torrents.toggleStartStop(torrent).subscribeOn(io()).observeOn(mainThread())
          .subscribeWith(ResultObserver(
              { data -> view.updateTorrent(data) },
              { err -> errorHandler.handleError(view, err) },
              { view.hideLoader() }
          ))
    })
    subscriptions.add(view.clickOnRefresh().subscribe {
      refresh(view)
    })
    subscriptions.add(view.clickOnAddTorrent().subscribe {
      view.selectTorrentFile()
    })
    subscriptions.add(view.torrentFileSelected().subscribe { uri ->
      view.showLoader()
      torrents.addTorrent(contentProvider.getBytes(uri)).subscribeOn(io()).observeOn(mainThread())
          .subscribeWith(EmptyResultObserver(
              { refresh(view) },
              { err -> errorHandler.handleError(view, err) },
              { view.hideLoader() }
          ))
    })

    subscriptions.add(refresh(view))
  }

  fun detach() {
    subscriptions.clear()
  }

  fun refresh(view: TorrentView): Disposable {
    view.showLoader()
    return torrents.list().subscribeOn(io()).observeOn(mainThread())
        .subscribeWith(ResultObserver(
            { data -> view.setTorrents(data) },
            { err -> errorHandler.handleError(view, err) },
            { view.hideLoader() }
        ))
  }
}