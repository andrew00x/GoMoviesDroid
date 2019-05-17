package com.andrew00x.gomoviesdroid.player

import android.annotation.SuppressLint
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

  fun attach(view: PlayerView, events: PlayerEventSource) {
    subscriptions.add(events.clickRefresh().subscribe {
      mainThread().scheduleDirect { view.showLoader() }
      player.getStatus().subscribeOn(io()).observeOn(mainThread())
          .subscribeWith(DefaultObserver<PlayerStatus>(
              { status -> view.hideLoader(); updateStatus(view, status) },
              { err -> view.hideLoader(); errorHandler.handleError(view, err) }
          ))
    })
    subscriptions.add(events.clickPlayPause().subscribe {
      mainThread().scheduleDirect { view.showLoader() }
      player.playPause().subscribeOn(io()).observeOn(mainThread())
          .subscribeWith(DefaultObserver<PlayerStatus>(
              { status -> view.hideLoader(); updateStatus(view, status) },
              { err -> view.hideLoader(); errorHandler.handleError(view, err) }
          ))
    })
    subscriptions.add(events.clickStop().subscribe {
      mainThread().scheduleDirect { view.showLoader() }
      player.stop().subscribeOn(io()).observeOn(mainThread())
          .subscribeWith(DefaultObserver<PlayerStatus>(
              { status -> view.hideLoader(); updateStatus(view, status) },
              { err -> view.hideLoader(); errorHandler.handleError(view, err) }
          ))
    })
    subscriptions.add(events.clickReplay().subscribe {
      mainThread().scheduleDirect { view.showLoader() }
      player.replay().subscribeOn(io()).observeOn(mainThread())
          .subscribeWith(DefaultObserver<PlayerStatus>(
              { status -> view.hideLoader(); updateStatus(view, status) },
              { err -> view.hideLoader(); errorHandler.handleError(view, err) }
          ))
    })
    subscriptions.add(events.clickForward10min().subscribe {
      mainThread().scheduleDirect { view.showLoader() }
      player.forward(10 * 60).subscribeOn(io()).observeOn(mainThread())
          .subscribeWith(DefaultObserver<PlayerStatus>(
              { status -> view.hideLoader(); updateStatus(view, status) },
              { err -> view.hideLoader(); errorHandler.handleError(view, err) }
          ))
    })
    subscriptions.add(events.clickRewind10min().subscribe {
      mainThread().scheduleDirect { view.showLoader() }
      player.rewind(10 * 60).subscribeOn(io()).observeOn(mainThread())
          .subscribeWith(DefaultObserver<PlayerStatus>(
              { status -> view.hideLoader(); updateStatus(view, status) },
              { err -> view.hideLoader(); errorHandler.handleError(view, err) }
          ))
    })
    subscriptions.add(events.clickForward30sec().subscribe {
      mainThread().scheduleDirect { view.showLoader() }
      player.forward(30).subscribeOn(io()).observeOn(mainThread())
          .subscribeWith(DefaultObserver<PlayerStatus>(
              { status -> view.hideLoader(); updateStatus(view, status) },
              { err -> view.hideLoader(); errorHandler.handleError(view, err) }
          ))
    })
    subscriptions.add(events.clickRewind30sec().subscribe {
      mainThread().scheduleDirect { view.showLoader() }
      player.rewind(30).subscribeOn(io()).observeOn(mainThread())
          .subscribeWith(DefaultObserver<PlayerStatus>(
              { status -> view.hideLoader(); updateStatus(view, status) },
              { err -> view.hideLoader(); errorHandler.handleError(view, err) }
          ))
    })
    subscriptions.add(events.clickVolumeUp().subscribe {
      mainThread().scheduleDirect { view.showLoader() }
      player.volumeUp().subscribeOn(io()).observeOn(mainThread())
          .subscribeWith(DefaultObserver<Volume>(
              { vol -> view.hideLoader(); view.showVolumeLevel(vol) },
              { err -> view.hideLoader(); errorHandler.handleError(view, err) }
          ))
    })
    subscriptions.add(events.clickVolumeDown().subscribe {
      mainThread().scheduleDirect { view.showLoader() }
      player.volumeDown().subscribeOn(io()).observeOn(mainThread())
          .subscribeWith(DefaultObserver<Volume>(
              { vol -> view.hideLoader(); view.showVolumeLevel(vol) },
              { err -> view.hideLoader(); errorHandler.handleError(view, err) }
          ))
    })
    subscriptions.add(events.clickNextAudioTrack().subscribe {
      mainThread().scheduleDirect { view.showLoader() }
      player.switchToNextAudioTrack().subscribeOn(io()).observeOn(mainThread())
          .subscribeWith(DefaultObserver<List<Stream>>(
              { tracks -> view.hideLoader(); view.showAudioStreams(tracks) },
              { err -> view.hideLoader(); errorHandler.handleError(view, err) }
          ))
    })
    subscriptions.add(events.clickPreviousAudioTrack().subscribe {
      mainThread().scheduleDirect { view.showLoader() }
      player.switchToPreviousAudioTrack().subscribeOn(io()).observeOn(mainThread())
          .subscribeWith(DefaultObserver<List<Stream>>(
              { tracks -> view.hideLoader(); view.showAudioStreams(tracks) },
              { err -> view.hideLoader(); errorHandler.handleError(view, err) }
          ))
    })
    subscriptions.add(events.clickNextSubtitles().subscribe {
      mainThread().scheduleDirect { view.showLoader() }
      player.switchToNextSubtitle().subscribeOn(io()).observeOn(mainThread())
          .subscribeWith(DefaultObserver<List<Stream>>(
              { subs -> view.hideLoader(); view.showSubtitles(subs) },
              { err -> view.hideLoader(); errorHandler.handleError(view, err) }
          ))
    })
    subscriptions.add(events.clickPreviousSubtitles().subscribe {
      mainThread().scheduleDirect { view.showLoader() }
      player.switchToPreviousSubtitle().subscribeOn(io()).observeOn(mainThread())
          .subscribeWith(DefaultObserver<List<Stream>>(
              { subs -> view.hideLoader(); view.showSubtitles(subs) },
              { err -> view.hideLoader(); errorHandler.handleError(view, err) }
          ))
    })
    subscriptions.add(events.clickToggleMute().subscribe {
      mainThread().scheduleDirect { view.showLoader() }
      player.toggleMute().subscribeOn(io()).observeOn(mainThread())
          .subscribeWith(DefaultObserver<PlayerStatus>(
              { status -> view.hideLoader(); updateStatus(view, status) },
              { err -> view.hideLoader(); errorHandler.handleError(view, err) }
          ))
    })
    subscriptions.add(events.clickToggleSubtitles().subscribe {
      mainThread().scheduleDirect { view.showLoader() }
      player.toggleSubtitles().subscribeOn(io()).observeOn(mainThread())
          .subscribeWith(DefaultObserver<PlayerStatus>(
              { status -> view.hideLoader(); updateStatus(view, status) },
              { err -> view.hideLoader(); errorHandler.handleError(view, err) }
          ))
    })
    subscriptions.add(events.seekPosition().debounce(150, TimeUnit.MILLISECONDS).subscribe { pos ->
      mainThread().scheduleDirect { view.showLoader() }
      player.setPosition(pos).subscribeOn(io()).observeOn(mainThread())
          .subscribeWith(DefaultObserver<PlayerStatus>(
              { status -> view.hideLoader(); updateStatus(view, status) },
              { err -> view.hideLoader(); errorHandler.handleError(view, err) }
          ))
    })
    refresh(view)
  }

  fun detach() {
    subscriptions.dispose()
  }

  @SuppressLint("CheckResult")
  fun refresh(view: PlayerView) {
    view.showLoader()
    player.getStatus().subscribeOn(io()).observeOn(mainThread())
        .subscribeWith(DefaultObserver<PlayerStatus>(
            { status -> view.hideLoader(); updateStatus(view, status) },
            { err -> view.hideLoader(); errorHandler.handleError(view, err) }
        ))
  }

  private fun updateStatus(view: PlayerView, status: PlayerStatus) {
    view.showPlayingNow(status.file.substringAfterLast('/'))
    view.showProgress(status.position, status.duration)
    view.showPaused(status.paused)
    view.showMuted(status.muted)
    view.showSubtitlesOff(status.subtitlesOff)
    if (status.stopped) {
      view.onPlaybackStopped()
    }
  }
}
