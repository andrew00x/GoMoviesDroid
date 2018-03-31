package com.andrew00x.gomoviesdroid.player

import com.andrew00x.gomoviesdroid.*
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers.io
import java.util.concurrent.TimeUnit

class PlayerPresenter(private val model: PlayerModel, private val view: PlayerView) : BasePresenter {

    private val subscriptions: CompositeDisposable = CompositeDisposable()

    override fun start() {
        subscriptions.add(model.clickRefresh().subscribe {
            model.status().subscribeOn(io()).observeOn(mainThread())
                    .subscribeWith(object : HttpResponseObserver<PlayerStatus>(view) {
                        override fun onSuccess(status: PlayerStatus) {
                            view.showPlayingNow(status.playing.substringAfterLast('/'))
                            view.showProgress(status.position, status.duration)
                        }
                    })
        })
        subscriptions.add(model.clickPlay().subscribe {
            model.play().subscribeOn(io()).observeOn(mainThread())
                    .subscribeWith(object : HttpResponseObserver<PlayerStatus>(view) {
                        override fun onSuccess(status: PlayerStatus) {}
                    })
        })
        subscriptions.add(model.clickPause().subscribe {
            model.pause().subscribeOn(io()).observeOn(mainThread())
                    .subscribeWith(object : HttpResponseObserver<PlayerStatus>(view) {
                        override fun onSuccess(status: PlayerStatus) {}
                    })
        })
        subscriptions.add(model.clickStop().subscribe {
            model.stop().subscribeOn(io()).observeOn(mainThread())
                    .subscribeWith(object : HttpResponseObserver<PlayerStatus>(view) {
                        override fun onSuccess(status: PlayerStatus) {
                            view.showPlayingNow("")
                            view.showProgress(0, 0)
                            view.onPlaybackStopped()
                        }
                    })
        })
        subscriptions.add(model.clickForward10min().subscribe {
            model.forward10min().subscribeOn(io()).observeOn(mainThread())
                    .subscribeWith(object : HttpResponseObserver<PlayerStatus>(view) {
                        override fun onSuccess(status: PlayerStatus) {
                            view.showProgress(status.position, status.duration)
                        }
                    })
        })
        subscriptions.add(model.clickRewind10min().subscribe {
            model.rewind10min().subscribeOn(io()).observeOn(mainThread())
                    .subscribeWith(object : HttpResponseObserver<PlayerStatus>(view) {
                        override fun onSuccess(status: PlayerStatus) {
                            view.showProgress(status.position, status.duration)
                        }
                    })
        })
        subscriptions.add(model.clickForward30sec().subscribe {
            model.forward30sec().subscribeOn(io()).observeOn(mainThread())
                    .subscribeWith(object : HttpResponseObserver<PlayerStatus>(view) {
                        override fun onSuccess(status: PlayerStatus) {
                            view.showProgress(status.position, status.duration)
                        }
                    })
        })
        subscriptions.add(model.clickRewind30sec().subscribe {
            model.rewind30sec().subscribeOn(io()).observeOn(mainThread())
                    .subscribeWith(object : HttpResponseObserver<PlayerStatus>(view) {
                        override fun onSuccess(status: PlayerStatus) {
                            view.showProgress(status.position, status.duration)
                        }
                    })
        })
        subscriptions.add(model.clickVolumeUp().subscribe {
            model.volumeUp().subscribeOn(io()).observeOn(mainThread())
                    .subscribeWith(object : HttpResponseObserver<Volume>(view) {
                        override fun onSuccess(body: Volume) {
                            view.showVolume(body)
                        }
                    })
        })
        subscriptions.add(model.clickVolumeDown().subscribe {
            model.volumeDown().subscribeOn(io()).observeOn(mainThread())
                    .subscribeWith(object : HttpResponseObserver<Volume>(view) {
                        override fun onSuccess(body: Volume) {
                            view.showVolume(body)
                        }
                    })
        })
        subscriptions.add(model.clickNextAudioTrack().subscribe {
            model.nextAudioTrack().subscribeOn(io()).observeOn(mainThread())
                    .subscribeWith(object : HttpResponseObserver<List<Stream>>(view) {
                        override fun onSuccess(body: List<Stream>) {
                            view.showAudioStreams(body)
                        }
                    })
        })
        subscriptions.add(model.clickPreviousAudioTrack().subscribe {
            model.previousAudioTrack().subscribeOn(io()).observeOn(mainThread())
                    .subscribeWith(object : HttpResponseObserver<List<Stream>>(view) {
                        override fun onSuccess(body: List<Stream>) {
                            view.showAudioStreams(body)
                        }
                    })
        })
        subscriptions.add(model.clickNextSubtitles().subscribe {
            model.nextSubtitles().subscribeOn(io()).observeOn(mainThread())
                    .subscribeWith(object : HttpResponseObserver<List<Stream>>(view) {
                        override fun onSuccess(body: List<Stream>) {
                            view.showSubtitles(body)
                        }
                    })
        })
        subscriptions.add(model.clickPreviousSubtitles().subscribe {
            model.previousSubtitles().subscribeOn(io()).observeOn(mainThread())
                    .subscribeWith(object : HttpResponseObserver<List<Stream>>(view) {
                        override fun onSuccess(body: List<Stream>) {
                            view.showSubtitles(body)
                        }
                    })
        })
        subscriptions.add(model.clickToggleMute().subscribe { state ->
            model.setMute(state).subscribeOn(io()).observeOn(mainThread()).subscribeWith(object : HttpResponseObserver<Any>(view) {
                override fun onSuccess(body: Any) {
                    model.saveMuteState(state)
                    view.showMute(state)
                }
            })
        })
        subscriptions.add(model.clickToggleSubtitles().subscribe {
            model.toggleSubtitles().subscribeOn(io()).observeOn(mainThread()).subscribeWith(object: HttpResponseObserver<Any>(view) {
                override fun onSuccess(body: Any) {}
            })
        })
        subscriptions.add(model.seekPosition().debounce(150, TimeUnit.MILLISECONDS).subscribe { pos ->
            model.setPosition(pos).subscribeOn(io()).observeOn(mainThread()).subscribeWith(object: HttpResponseObserver<PlayerStatus>(view) {
                override fun onSuccess(status: PlayerStatus) {
                    view.showProgress(status.position, status.duration)
                }
            })
        })
        refresh()
    }

    override fun stop() {
        subscriptions.dispose()
    }

    override fun refresh() {
        model.status().subscribeOn(io()).observeOn(mainThread())
                .subscribeWith(object : HttpResponseObserver<PlayerStatus>(view) {
                    override fun onSuccess(status: PlayerStatus) {
                        view.showPlayingNow(status.playing.substringAfterLast('/'))
                        view.showProgress(status.position, status.duration)
                    }
                })
    }
}