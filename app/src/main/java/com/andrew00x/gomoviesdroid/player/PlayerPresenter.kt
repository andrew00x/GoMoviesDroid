package com.andrew00x.gomoviesdroid.player

import com.andrew00x.gomoviesdroid.*
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers.io
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PlayerPresenter @Inject constructor(
    private val player: PlayerModel,
    private val errorHandler: ErrorHandler) {
  private val subscriptions: CompositeDisposable = CompositeDisposable()

  fun attach(view: PlayerView) {
    subscriptions.add(view.clickRefresh().subscribe {
      view.showLoader()
      player.getStatus().subscribeOn(io()).observeOn(mainThread())
          .subscribeWith(DefaultObserver<PlayerStatus>(
              { status -> updateStatus(view, status) },
              { err -> errorHandler.handleError(view, err) },
              { view.hideLoader() }
          ))
    })
    subscriptions.add(view.clickPlayPause().subscribe {
      view.showLoader()
      player.playPause().subscribeOn(io()).observeOn(mainThread())
          .subscribeWith(DefaultObserver<PlayerStatus>(
              { status -> updateStatus(view, status) },
              { err -> errorHandler.handleError(view, err) },
              { view.hideLoader() }
          ))
    })
    subscriptions.add(view.clickStop().subscribe {
      view.showLoader()
      player.stop().subscribeOn(io()).observeOn(mainThread())
          .subscribeWith(DefaultObserver<PlayerStatus>(
              { status -> updateStatus(view, status) },
              { err -> errorHandler.handleError(view, err) },
              { view.hideLoader() }
          ))
    })
    subscriptions.add(view.clickReplay().subscribe {
      view.showLoader()
      player.replay().subscribeOn(io()).observeOn(mainThread())
          .subscribeWith(DefaultObserver<PlayerStatus>(
              { status -> updateStatus(view, status) },
              { err -> errorHandler.handleError(view, err) },
              { view.hideLoader() }
          ))
    })
    subscriptions.add(view.clickForward10min().subscribe {
      view.showLoader()
      player.forward(10 * 60).subscribeOn(io()).observeOn(mainThread())
          .subscribeWith(DefaultObserver<PlayerStatus>(
              { status -> updateStatus(view, status) },
              { err -> errorHandler.handleError(view, err) },
              { view.hideLoader() }
          ))
    })
    subscriptions.add(view.clickRewind10min().subscribe {
      view.showLoader()
      player.rewind(10 * 60).subscribeOn(io()).observeOn(mainThread())
          .subscribeWith(DefaultObserver<PlayerStatus>(
              { status -> updateStatus(view, status) },
              { err -> errorHandler.handleError(view, err) },
              { view.hideLoader() }
          ))
    })
    subscriptions.add(view.clickForward30sec().subscribe {
      view.showLoader()
      player.forward(30).subscribeOn(io()).observeOn(mainThread())
          .subscribeWith(DefaultObserver<PlayerStatus>(
              { status -> updateStatus(view, status) },
              { err -> errorHandler.handleError(view, err) },
              { view.hideLoader() }
          ))
    })
    subscriptions.add(view.clickRewind30sec().subscribe {
      view.showLoader()
      player.rewind(30).subscribeOn(io()).observeOn(mainThread())
          .subscribeWith(DefaultObserver<PlayerStatus>(
              { status -> updateStatus(view, status) },
              { err -> errorHandler.handleError(view, err) },
              { view.hideLoader() }
          ))
    })
    subscriptions.add(view.clickVolumeUp().subscribe {
      view.showLoader()
      player.volumeUp().subscribeOn(io()).observeOn(mainThread())
          .subscribeWith(DefaultObserver<Volume>(
              { vol -> view.showVolumeLevel(vol) },
              { err -> errorHandler.handleError(view, err) },
              { view.hideLoader() }
          ))
    })
    subscriptions.add(view.clickVolumeDown().subscribe {
      view.showLoader()
      player.volumeDown().subscribeOn(io()).observeOn(mainThread())
          .subscribeWith(DefaultObserver<Volume>(
              { vol -> view.showVolumeLevel(vol) },
              { err -> errorHandler.handleError(view, err) },
              { view.hideLoader() }
          ))
    })
    subscriptions.add(view.clickNextAudioTrack().subscribe {
      view.showLoader()
      player.switchToNextAudioTrack().subscribeOn(io()).observeOn(mainThread())
          .subscribeWith(DefaultObserver<List<Stream>>(
              { tracks -> view.showAudioStreams(tracks) },
              { err -> errorHandler.handleError(view, err) },
              { view.hideLoader() }
          ))
    })
    subscriptions.add(view.clickPreviousAudioTrack().subscribe {
      view.showLoader()
      player.switchToPreviousAudioTrack().subscribeOn(io()).observeOn(mainThread())
          .subscribeWith(DefaultObserver<List<Stream>>(
              { tracks -> view.showAudioStreams(tracks) },
              { err -> errorHandler.handleError(view, err) },
              { view.hideLoader() }
          ))
    })
    subscriptions.add(view.clickNextSubtitles().subscribe {
      view.showLoader()
      player.switchToNextSubtitle().subscribeOn(io()).observeOn(mainThread())
          .subscribeWith(DefaultObserver<List<Stream>>(
              { subs -> view.showSubtitles(subs) },
              { err -> errorHandler.handleError(view, err) },
              { view.hideLoader() }
          ))
    })
    subscriptions.add(view.clickPreviousSubtitles().subscribe {
      view.showLoader()
      player.switchToPreviousSubtitle().subscribeOn(io()).observeOn(mainThread())
          .subscribeWith(DefaultObserver<List<Stream>>(
              { subs -> view.showSubtitles(subs) },
              { err -> errorHandler.handleError(view, err) },
              { view.hideLoader() }
          ))
    })
    subscriptions.add(view.clickToggleMute().subscribe {
      view.showLoader()
      player.toggleMute().subscribeOn(io()).observeOn(mainThread())
          .subscribeWith(DefaultObserver<PlayerStatus>(
              { status -> updateStatus(view, status) },
              { err -> errorHandler.handleError(view, err) },
              { view.hideLoader() }
          ))
    })
    subscriptions.add(view.clickToggleSubtitles().subscribe {
      view.showLoader()
      player.toggleSubtitles().subscribeOn(io()).observeOn(mainThread())
          .subscribeWith(DefaultObserver<PlayerStatus>(
              { status -> updateStatus(view, status) },
              { err -> errorHandler.handleError(view, err) },
              { view.hideLoader() }
          ))
    })
    subscriptions.add(view.seekPosition().debounce(150, TimeUnit.MILLISECONDS).subscribe { pos ->
      mainThread().scheduleDirect { view.showLoader() }
      player.setPosition(pos).subscribeOn(io()).observeOn(mainThread())
          .subscribeWith(DefaultObserver<PlayerStatus>(
              { status -> updateStatus(view, status) },
              { err -> errorHandler.handleError(view, err) },
              { view.hideLoader() }
          ))
    })
  }

  fun detach() {
    subscriptions.clear()
  }

  private fun updateStatus(view: PlayerView, status: PlayerStatus) {
    view.showPlayingNow(if (status.stopped) "" else status.file.substringAfterLast('/'))
    view.showProgress(
        if (status.stopped) 0 else status.position,
        if (status.stopped) 0 else status.duration
    )
    view.showPaused(status.paused)
    view.showMuted(status.muted)
    view.showSubtitlesOff(status.subtitlesOff)
  }
}
