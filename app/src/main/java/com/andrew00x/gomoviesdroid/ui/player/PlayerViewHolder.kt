package com.andrew00x.gomoviesdroid.ui.player

import android.view.View
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import com.andrew00x.gomoviesdroid.R

class PlayerViewHolder(view: View) {
  val playing: TextView = view.findViewById(R.id.player_playing_now)
  val toggleMute: ImageButton = view.findViewById(R.id.player_toggle_mute)
  val seekBar: SeekBar = view.findViewById(R.id.player_seek_bar)
  val timePassed: TextView = view.findViewById(R.id.player_time_passed)
  val timeLeft: TextView = view.findViewById(R.id.player_time_left)
  val refresh: ImageButton = view.findViewById(R.id.player_refresh)
  val playPause: ImageButton = view.findViewById(R.id.player_play_pause)
  val replay: ImageButton = view.findViewById(R.id.player_replay)
  val stop: ImageButton = view.findViewById(R.id.player_stop)
  val forward10min: ImageButton = view.findViewById(R.id.player_arrow_up)
  val rewind10min: ImageButton = view.findViewById(R.id.player_arrow_down)
  val forward30sec: ImageButton = view.findViewById(R.id.player_arrow_right)
  val rewind30sec: ImageButton = view.findViewById(R.id.player_arrow_left)
  val volumeUp: ImageButton = view.findViewById(R.id.player_volume_up)
  val volumeDown: ImageButton = view.findViewById(R.id.player_volume_down)
  val nextAudioTrack: ImageButton = view.findViewById(R.id.player_next_audio_track)
  val previousAudioTrack: ImageButton = view.findViewById(R.id.player_previous_audio_track)
  val nextSubtitles: ImageButton = view.findViewById(R.id.player_next_subtitles)
  val previousSubtitles: ImageButton = view.findViewById(R.id.player_previous_subtitles)
  val toggleSubtitles: ImageButton = view.findViewById(R.id.player_toggle_subtitles)
}