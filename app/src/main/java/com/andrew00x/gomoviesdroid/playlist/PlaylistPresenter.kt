package com.andrew00x.gomoviesdroid.playlist

import com.andrew00x.gomoviesdroid.ErrorHandler
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class PlaylistPresenter @Inject constructor(
    private val model: PlaylistModel,
    private val errorHandler: ErrorHandler) {
  private val subscriptions: CompositeDisposable = CompositeDisposable()

  lateinit var playlist: Playlist

  fun attach(view: PlaylistView) {
    subscriptions.add(view.clickOnAddItems().subscribe {
      view.startAddItems()
    })
    subscriptions.add(view.clickOnDelete().subscribe { item ->
      try {
        view.setPlaylist(model.delete(playlist, item))
      } catch (e: Exception) {
        errorHandler.handleError(view, e)
      }
    })
    refresh(view)
  }

  fun refresh(view: PlaylistView) {
    try {
      view.setPlaylist(model.getAll(playlist))
    } catch (e: Exception) {
      errorHandler.handleError(view, e)
    }
  }

  fun detach() {
    subscriptions.clear()
  }
}
