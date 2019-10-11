package com.andrew00x.gomoviesdroid.player

import com.andrew00x.gomoviesdroid.ui.BaseView
import com.andrew00x.gomoviesdroid.Stream
import com.andrew00x.gomoviesdroid.Volume
import io.reactivex.Observable

interface PlayerView : BaseView {
  fun clickForward10min(): Observable<Any>
  fun clickForward30sec(): Observable<Any>
  fun clickNextAudioTrack(): Observable<Any>
  fun clickNextSubtitles(): Observable<Any>
  fun clickReplay(): Observable<Any>
  fun clickPlayPause(): Observable<Any>
  fun clickPreviousAudioTrack(): Observable<Any>
  fun clickPreviousSubtitles(): Observable<Any>
  fun clickRefresh(): Observable<Any>
  fun clickRewind10min(): Observable<Any>
  fun clickRewind30sec(): Observable<Any>
  fun clickStop(): Observable<Any>
  fun clickToggleMute(): Observable<Any>
  fun clickToggleSubtitles(): Observable<Any>
  fun clickVolumeDown(): Observable<Any>
  fun clickVolumeUp(): Observable<Any>
  fun seekPosition(): Observable<Int>
  fun showPlayingNow(playing: String)
  fun showProgress(position: Int, duration: Int)
  fun showPaused(paused: Boolean)
  fun showMuted(muted: Boolean)
  fun showVolumeLevel(vol: Volume)
  fun showAudioStreams(audios: List<Stream>)
  fun showSubtitles(subtitles: List<Stream>)
  fun showSubtitlesOff(off: Boolean)
}
