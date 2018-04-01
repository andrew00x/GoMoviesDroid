package com.andrew00x.gomoviesdroid.player

import android.view.View
import android.widget.SeekBar
import com.andrew00x.gomoviesdroid.GomoviesService
import com.andrew00x.gomoviesdroid.PlayerStatus
import com.andrew00x.gomoviesdroid.Stream
import com.andrew00x.gomoviesdroid.Volume
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import retrofit2.Response

enum class MuteState {
    ON,OFF;

    fun toggle(): MuteState {
        return if (this == ON) OFF else ON
    }
}

class PlayerModel(private val service: GomoviesService,
                  private val refresh: View,
                  private val play: View,
                  private val pause: View,
                  private val stop: View,
                  private val forward10min: View,
                  private val rewind10min: View,
                  private val forward30sec: View,
                  private val rewind30sec: View,
                  private val volumeUp: View,
                  private val volumeDown: View,
                  private val nextAudioTrack: View,
                  private val previousAudioTrack: View,
                  private val nextSubtitles: View,
                  private val previousSubtitles: View,
                  private val toggleMute: View,
                  private val toggleSubtitles: View,
                  private val seekBar: SeekBar
) {

    init {
        toggleMute.tag = MuteState.OFF
    }

    fun clickForward10min(): Observable<Any> {
        return RxView.clicks(forward10min)
    }

    fun clickForward30sec(): Observable<Any> {
        return RxView.clicks(forward30sec)
    }

    fun clickNextAudioTrack(): Observable<Any> {
        return RxView.clicks(nextAudioTrack)
    }

    fun clickNextSubtitles(): Observable<Any> {
        return RxView.clicks(nextSubtitles)
    }

    fun clickPause(): Observable<Any> {
        return RxView.clicks(pause)
    }

    fun clickPlay(): Observable<Any> {
        return RxView.clicks(play)
    }

    fun clickPreviousAudioTrack(): Observable<Any> {
        return RxView.clicks(previousAudioTrack)
    }

    fun clickPreviousSubtitles(): Observable<Any> {
        return RxView.clicks(previousSubtitles)
    }

    fun clickRefresh(): Observable<Any> {
        return RxView.clicks(refresh)
    }

    fun clickRewind10min(): Observable<Any> {
        return RxView.clicks(rewind10min)
    }

    fun clickRewind30sec(): Observable<Any> {
        return RxView.clicks(rewind30sec)
    }

    fun clickStop(): Observable<Any> {
        return RxView.clicks(stop)
    }

    fun clickToggleMute(): Observable<MuteState> {
        return Observable.create<MuteState> { emitter ->
            emitter.setCancellable { toggleMute.setOnClickListener(null) }
            toggleMute.setOnClickListener { v -> emitter.onNext((v.tag as MuteState).toggle()) }
        }
    }

    fun clickToggleSubtitles(): Observable<Any> {
        return RxView.clicks(toggleSubtitles)
    }

    fun clickVolumeDown(): Observable<Any> {
        return RxView.clicks(volumeDown)
    }

    fun clickVolumeUp(): Observable<Any> {
        return RxView.clicks(volumeUp)
    }

    fun seekPosition(): Observable<Int> {
        return Observable.create<Int> { emitter ->
            emitter.setCancellable { seekBar.setOnSeekBarChangeListener(null) }
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) emitter.onNext(progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }
    }

    fun forward10min(): Observable<PlayerStatus> {
        return service.seek(10 * 60)
    }

    fun forward30sec(): Observable<PlayerStatus> {
        return service.seek(30)
    }

    fun nextAudioTrack(): Observable<List<Stream>> {
        return service.nextAudioTrack()
    }

    fun nextSubtitles(): Observable<List<Stream>> {
        return service.nextSubtitles()
    }

    fun pause(): Observable<PlayerStatus> {
        return service.pause()
    }

    fun play(): Observable<PlayerStatus> {
        return service.play()
    }

    fun previousAudioTrack(): Observable<List<Stream>> {
        return service.previousAudioTrack()
    }

    fun previousSubtitles(): Observable<List<Stream>> {
        return service.previousSubtitles()
    }

    fun rewind10min(): Observable<PlayerStatus> {
        return service.seek(-10 * 60)
    }

    fun rewind30sec(): Observable<PlayerStatus> {
        return service.seek(-30)
    }

    fun saveMuteState(state: MuteState) {
        toggleMute.tag = state
    }

    fun setMute(state: MuteState): Observable<Response<Void>> {
        return if (state == MuteState.ON) service.mute() else service.unmute()
    }

    fun setPosition(pos: Int): Observable<PlayerStatus> {
        return service.setPosition(pos)
    }

    fun status(): Observable<PlayerStatus> {
        return service.status()
    }

    fun stop(): Observable<PlayerStatus> {
        return service.stop()
    }

    fun toggleSubtitles(): Observable<Response<Void>> {
        return service.toggleSubtitles()
    }

    fun volumeDown(): Observable<Volume> {
        return service.volumeDown()
    }

    fun volumeUp(): Observable<Volume> {
        return service.volumeUp()
    }
}

