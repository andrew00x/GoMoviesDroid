package com.andrew00x.gomoviesdroid.playlist

import com.andrew00x.gomoviesdroid.ErrorHandler
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class PlaylistNamePresenter @Inject constructor(
    private val model: PlaylistModel,
    private val errorHandler: ErrorHandler) {
  private val subscriptions: CompositeDisposable = CompositeDisposable()

  lateinit var playlist: Playlist

  fun attach(view: PlaylistNameView) {
    subscriptions.add(view.clickOnSave().subscribe {
      playlist.name = view.getName()
      try {
        model.save(playlist)
      } catch (e: Exception) {
        errorHandler.handleError(view, e)
      }
    })
    view.setName(playlist.name)
  }

  fun detach() {
    subscriptions.clear()
  }
}
