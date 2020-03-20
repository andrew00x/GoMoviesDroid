package com.andrew00x.gomoviesdroid.playlist

import com.andrew00x.gomoviesdroid.ResultObserver
import com.andrew00x.gomoviesdroid.ErrorHandler
import com.andrew00x.gomoviesdroid.queue.QueueItem
import com.andrew00x.gomoviesdroid.queue.QueueModel
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers.io
import javax.inject.Inject

class PlaylistListPresenter @Inject constructor(
    private val playlistModel: PlaylistModel,
    private val queueModel: QueueModel,
    private val errorHandler: ErrorHandler) {
  private val subscriptions: CompositeDisposable = CompositeDisposable()

  fun attach(view: PlaylistListView) {
    subscriptions.add(
        view.clickOnPlaylist().subscribe { playlist -> view.showPlaylist(playlist) }
    )
    subscriptions.add(
        view.longClickOnPlaylist().subscribe { playlist ->
          view.showLoader()
          val items = playlistModel.getAll(playlist).map { item -> item.file }
          queueModel.enqueue(items, true).subscribeOn(io()).observeOn(mainThread())
              .subscribeWith(ResultObserver<List<QueueItem>>(
                  whenError = { err -> errorHandler.handleError(view, err) },
                  always = { view.hideLoader() }
              ))
        }
    )
    subscriptions.add(
        view.clickOnEdit().subscribe { playlist -> view.editPlaylist(playlist) }
    )
    subscriptions.add(
        view.clickOnDelete().subscribe { playlist ->
          try {
            view.setPlaylistList(playlistModel.delete(playlist))
          } catch (e: Exception) {
            errorHandler.handleError(view, e)
          }
        }
    )
    subscriptions.add(
        view.clickOnCreate().subscribe {
          try {
            playlistModel.create()
            view.setPlaylistList(playlistModel.getAll())
          } catch (e: Exception) {
            errorHandler.handleError(view, e)
          }
        }
    )
    try {
      view.setPlaylistList(playlistModel.getAll())
    } catch (e: Exception) {
      errorHandler.handleError(view, e)
    }
  }

  fun detach() {
    subscriptions.clear()
  }
}
