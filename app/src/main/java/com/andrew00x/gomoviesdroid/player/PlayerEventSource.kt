package com.andrew00x.gomoviesdroid.player

import io.reactivex.Observable

interface PlayerEventSource {
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
}