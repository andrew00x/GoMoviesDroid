package com.andrew00x.gomoviesdroid.ui.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.andrew00x.gomoviesdroid.*
import com.andrew00x.gomoviesdroid.player.PlayerPresenter
import com.andrew00x.gomoviesdroid.player.PlayerView
import com.andrew00x.gomoviesdroid.ui.BaseFragment
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxSeekBar
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class PlayerFragment : BaseFragment(), PlayerView {
  companion object {
    fun newInstance(): PlayerFragment = PlayerFragment()
  }

  private val onRefresh = PublishSubject.create<Any>()
  private lateinit var components: PlayerViewHolder
  @Inject lateinit var presenter: PlayerPresenter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    (activity!!.application as GomoviesApplication).component.inject(this)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    val view = inflater.inflate(R.layout.fragment_player, container, false)!!
    components = PlayerViewHolder(view)
    return view
  }

  override fun onResume() {
    super.onResume()
    components.refresh.setOnClickListener { onRefresh.onNext(Unit) }
    presenter.attach(this)
    onRefresh.onNext(Unit)
  }

  override fun onPause() {
    presenter.detach()
    components.refresh.setOnClickListener(null)
    super.onPause()
  }

  override fun showPlayingNow(playing: String) {
    components.playing.text = playing
  }

  override fun showProgress(position: Int, duration: Int) {
    components.timePassed.text = formatTime(position)
    components.timeLeft.text = formatTime(duration - position)
    components.seekBar.max = duration
    components.seekBar.progress = position
  }

  private fun formatTime(sec: Int): String {
    val h = sec / 3600
    val m = (sec % 3600) / 60
    val s = sec % 60
    return "%02d:%02d:%02d".format(h, m, s)
  }

  override fun showPaused(paused: Boolean) {
    components.playPause.setImageDrawable(when (paused) {
      true -> resources.getDrawable(R.drawable.ic_play_24dp, null)
      false -> resources.getDrawable(R.drawable.ic_pause_24dp, null)
    })
  }

  override fun showVolumeLevel(vol: Volume) {
    showInfo(vol.toDisplayString())
  }

  override fun showMuted(muted: Boolean) {
    components.toggleMute.background = when (muted) {
      true -> resources.getDrawable(R.drawable.oval_orange, null)
      else -> resources.getDrawable(R.drawable.oval_gray, null)
    }
  }

  override fun showAudioStreams(audios: List<Stream>) {
    val active = audios.firstOrNull { it.active }
    if (active != null) {
      showInfo("Audio: ${active.toDisplayString()}")
    }
  }

  override fun showSubtitles(subtitles: List<Stream>) {
    val active = subtitles.firstOrNull { it.active }
    if (active != null) {
      showInfo("Subtitles: ${active.toDisplayString()}")
    }
  }

  override fun showSubtitlesOff(off: Boolean) {
    components.toggleSubtitles.background = when (off) {
      true -> resources.getDrawable(R.drawable.oval_orange, null)
      else -> resources.getDrawable(R.drawable.oval_gray, null)
    }
  }

  override fun clickForward10min(): Observable<Any> = RxView.clicks(components.forward10min)

  override fun clickForward30sec(): Observable<Any> = RxView.clicks(components.forward30sec)

  override fun clickNextAudioTrack(): Observable<Any> = RxView.clicks(components.nextAudioTrack)

  override fun clickNextSubtitles(): Observable<Any> = RxView.clicks(components.nextSubtitles)

  override fun clickPreviousAudioTrack(): Observable<Any> = RxView.clicks(components.previousAudioTrack)

  override fun clickPreviousSubtitles(): Observable<Any> = RxView.clicks(components.previousSubtitles)

  override fun clickRefresh(): Observable<Any> = onRefresh

  override fun clickRewind10min(): Observable<Any> = RxView.clicks(components.rewind10min)

  override fun clickRewind30sec(): Observable<Any> = RxView.clicks(components.rewind30sec)

  override fun clickPlayPause(): Observable<Any> = RxView.clicks(components.playPause)

  override fun clickStop(): Observable<Any> = RxView.clicks(components.stop)

  override fun clickReplay(): Observable<Any> = RxView.clicks(components.replay)

  override fun clickToggleMute(): Observable<Any> = RxView.clicks(components.toggleMute)

  override fun clickToggleSubtitles(): Observable<Any> = RxView.clicks(components.toggleSubtitles)

  override fun clickVolumeDown(): Observable<Any> = RxView.clicks(components.volumeDown)

  override fun clickVolumeUp(): Observable<Any> = RxView.clicks(components.volumeUp)

  override fun seekPosition(): Observable<Int> = RxSeekBar.userChanges(components.seekBar).skip(1)
}
