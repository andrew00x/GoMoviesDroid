package com.andrew00x.gomoviesdroid.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.andrew00x.gomoviesdroid.*
import com.andrew00x.gomoviesdroid.player.PlayerEventSource
import com.andrew00x.gomoviesdroid.player.PlayerPresenter
import com.andrew00x.gomoviesdroid.player.PlayerView
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import javax.inject.Inject

class PlayerFragment : PlayerView, PlayerEventSource, BaseFragment() {
  private lateinit var playing: TextView
  private lateinit var toggleMute: ImageButton
  private lateinit var seekBar: SeekBar
  private lateinit var timePassed: TextView
  private lateinit var timeLeft: TextView
  private lateinit var refresh: ImageButton
  private lateinit var playPause: ImageButton
  private lateinit var replay: ImageButton
  private lateinit var stop: ImageButton
  private lateinit var forward10min: ImageButton
  private lateinit var rewind10min: ImageButton
  private lateinit var forward30sec: ImageButton
  private lateinit var rewind30sec: ImageButton
  private lateinit var volumeUp: ImageButton
  private lateinit var volumeDown: ImageButton
  private lateinit var nextAudioTrack: ImageButton
  private lateinit var previousAudioTrack: ImageButton
  private lateinit var nextSubtitles: ImageButton
  private lateinit var previousSubtitles: ImageButton
  private lateinit var toggleSubtitles: ImageButton

  @Inject lateinit var presenter: PlayerPresenter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    (activity.application as GomoviesApplication).component.injectInto(this)
  }

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
    val view = inflater?.inflate(R.layout.fragment_player, container, false)!!
    initView(view)
    presenter.attach(this, this)
    return view
  }

  private fun initView(view: View) {
    playing = view.findViewById(R.id.playing_resource)
    toggleMute = view.findViewById(R.id.toggle_mute)
    seekBar = view.findViewById(R.id.seek_bar)
    timePassed = view.findViewById(R.id.time_passed)
    timeLeft = view.findViewById(R.id.time_left)
    refresh = view.findViewById(R.id.refresh)
    playPause = view.findViewById(R.id.playPause)
    replay = view.findViewById(R.id.replay)
    stop = view.findViewById(R.id.stop)
    forward10min = view.findViewById(R.id.arrow_up)
    rewind10min = view.findViewById(R.id.arrow_down)
    forward30sec = view.findViewById(R.id.arrow_right)
    rewind30sec = view.findViewById(R.id.arrow_left)
    volumeUp = view.findViewById(R.id.volume_up)
    volumeDown = view.findViewById(R.id.volume_down)
    nextAudioTrack = view.findViewById(R.id.next_audio_track)
    previousAudioTrack = view.findViewById(R.id.previous_audio_track)
    nextSubtitles = view.findViewById(R.id.next_subtitles)
    previousSubtitles = view.findViewById(R.id.previous_subtitles)
    toggleSubtitles = view.findViewById(R.id.toggle_subtitles)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    presenter.detach()
  }

  override fun refresh() {
    presenter.refresh(this)
  }

  override fun showPlayingNow(playing: String) {
    this.playing.text = playing
  }

  override fun showProgress(position: Int, duration: Int) {
    timePassed.text = formatTime(position)
    timeLeft.text = formatTime(duration - position)
    seekBar.max = duration
    seekBar.progress = position
  }

  private fun formatTime(sec: Int): String {
    val h = sec / 3600
    val m = (sec % 3600) / 60
    val s = sec % 60
    return "%02d:%02d:%02d".format(h, m, s)
  }

  override fun showPaused(paused: Boolean) {
    playPause.setImageDrawable(when (paused) {
      true -> activity.resources.getDrawable(R.drawable.ic_play_black_24dp, null)
      false -> activity.resources.getDrawable(R.drawable.ic_pause_black_24dp, null)
    })
  }

  override fun showVolumeLevel(vol: Volume) {
    Toast.makeText(activity, vol.toDisplayString(), Toast.LENGTH_SHORT).show()
  }

  override fun showMuted(muted: Boolean) {
    toggleMute.background = when (muted) {
      true -> activity.resources.getDrawable(R.drawable.round_button_orange, null)
      else -> activity.resources.getDrawable(R.drawable.round_button_gray, null)
    }
  }

  override fun showAudioStreams(audios: List<Stream>) {
    val active = audios.firstOrNull { it.active }
    if (active != null) {
      Toast.makeText(activity, "Audio: ${active.toDisplayString()}", Toast.LENGTH_SHORT).show()
    }
  }

  override fun showSubtitles(subtitles: List<Stream>) {
    val active = subtitles.firstOrNull { it.active }
    if (active != null) {
      Toast.makeText(activity, "Subtitles: ${active.toDisplayString()}", Toast.LENGTH_SHORT).show()
    }
  }

  override fun showSubtitlesOff(off: Boolean) {
    toggleSubtitles.background = when (off) {
      true -> activity.resources.getDrawable(R.drawable.round_button_orange, null)
      else -> activity.resources.getDrawable(R.drawable.round_button_gray, null)
    }
  }

  override fun onPlaybackStarted() {
    if (activity is PlaybackListener) (activity as PlaybackListener).onPlaybackStarted()
  }

  override fun onPlaybackStopped() {
    if (activity is PlaybackListener) (activity as PlaybackListener).onPlaybackStopped()
  }

  override fun clickForward10min(): Observable<Any> {
    return RxView.clicks(forward10min)
  }

  override fun clickForward30sec(): Observable<Any> {
    return RxView.clicks(forward30sec)
  }

  override fun clickNextAudioTrack(): Observable<Any> {
    return RxView.clicks(nextAudioTrack)
  }

  override fun clickNextSubtitles(): Observable<Any> {
    return RxView.clicks(nextSubtitles)
  }

  override fun clickPreviousAudioTrack(): Observable<Any> {
    return RxView.clicks(previousAudioTrack)
  }

  override fun clickPreviousSubtitles(): Observable<Any> {
    return RxView.clicks(previousSubtitles)
  }

  override fun clickRefresh(): Observable<Any> {
    return RxView.clicks(refresh)
  }

  override fun clickRewind10min(): Observable<Any> {
    return RxView.clicks(rewind10min)
  }

  override fun clickRewind30sec(): Observable<Any> {
    return RxView.clicks(rewind30sec)
  }

  override fun clickPlayPause(): Observable<Any> {
    return RxView.clicks(playPause)
  }

  override fun clickStop(): Observable<Any> {
    return RxView.clicks(stop)
  }

  override fun clickReplay(): Observable<Any> {
    return RxView.clicks(replay)
  }

  override fun clickToggleMute(): Observable<Any> {
    return RxView.clicks(toggleMute)
  }

  override fun clickToggleSubtitles(): Observable<Any> {
    return RxView.clicks(toggleSubtitles)
  }

  override fun clickVolumeDown(): Observable<Any> {
    return RxView.clicks(volumeDown)
  }

  override fun clickVolumeUp(): Observable<Any> {
    return RxView.clicks(volumeUp)
  }

  override fun seekPosition(): Observable<Int> {
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
}
