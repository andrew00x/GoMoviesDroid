package com.andrew00x.gomoviesdroid.player

import com.andrew00x.gomoviesdroid.BaseView
import com.andrew00x.gomoviesdroid.PlaybackListener
import com.andrew00x.gomoviesdroid.Stream
import com.andrew00x.gomoviesdroid.Volume

interface PlayerView : BaseView, PlaybackListener {
  fun showPlayingNow(playing: String)
  fun showProgress(position: Int, duration: Int)
  fun showPaused(paused: Boolean)
  fun showMuted(muted: Boolean)
  fun showVolumeLevel(vol: Volume)
  fun showAudioStreams(audios: List<Stream>)
  fun showSubtitles(subtitles: List<Stream>)
  fun showSubtitlesOff(off: Boolean)
}
