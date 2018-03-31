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
import com.andrew00x.gomoviesdroid.player.MuteState
import com.andrew00x.gomoviesdroid.player.PlayerModel
import com.andrew00x.gomoviesdroid.player.PlayerPresenter
import com.andrew00x.gomoviesdroid.player.PlayerView
import javax.inject.Inject

class PlayerFragment : PlayerView, BaseFragment() {
    @Inject lateinit var service: GomoviesService

    private lateinit var playing: TextView
    private lateinit var toggleMute: ImageButton
    private lateinit var seekBar: SeekBar
    private lateinit var timePassed: TextView
    private lateinit var timeLeft: TextView

    private var presenter: PlayerPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity.application as GomoviesApplication).component.injectInto(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater?.inflate(R.layout.fragment_player, container, false)!!

        playing = view.findViewById(R.id.playing_resource)
        toggleMute = view.findViewById(R.id.toggle_mute)
        seekBar = view.findViewById(R.id.seek_bar)
        timePassed = view.findViewById(R.id.time_passed)
        timeLeft = view.findViewById(R.id.time_left)
        val refresh = view.findViewById<ImageButton>(R.id.refresh)
        val play = view.findViewById<ImageButton>(R.id.play)
        val pause = view.findViewById<ImageButton>(R.id.pause)
        val stop = view.findViewById<ImageButton>(R.id.stop)
        val forward10min = view.findViewById<ImageButton>(R.id.arrow_up)
        val rewind10min = view.findViewById<ImageButton>(R.id.arrow_down)
        val forward30sec = view.findViewById<ImageButton>(R.id.arrow_right)
        val rewind30sec = view.findViewById<ImageButton>(R.id.arrow_left)
        val volumeUp = view.findViewById<ImageButton>(R.id.volume_up)
        val volumeDown = view.findViewById<ImageButton>(R.id.volume_down)
        val nextAudioTrack = view.findViewById<ImageButton>(R.id.next_audio_track)
        val previousAudioTrack = view.findViewById<ImageButton>(R.id.previous_audio_track)
        val nextSubtitles = view.findViewById<ImageButton>(R.id.next_subtitles)
        val previousSubtitles = view.findViewById<ImageButton>(R.id.previous_subtitles)
        val toggleSubtitles = view.findViewById<ImageButton>(R.id.toggle_subtitles)

        val player = PlayerModel(service, refresh, play, pause, stop, forward10min, rewind10min, forward30sec, rewind30sec, volumeUp, volumeDown, nextAudioTrack, previousAudioTrack, nextSubtitles, previousSubtitles, toggleMute, toggleSubtitles, seekBar)
        val presenter = PlayerPresenter(player, this)
        presenter.start()
        this.presenter = presenter

        return view
    }

    override fun getPresenter(): BasePresenter? {
        return presenter
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

    override fun showVolume(vol: Volume) {
        // https://github.com/popcornmix/omxplayer#volume-rw
        val dB = 20.0 * Math.log10(vol.volume.toDouble())
        Toast.makeText(activity, "Volume: %.1fdB".format(dB), Toast.LENGTH_SHORT).show()
    }

    override fun showAudioStreams(audios: List<Stream>) {
        val active = audios.firstOrNull { it -> it.active }
        if (active != null) {
            Toast.makeText(activity, "Audio: ${active.toDisplayString()}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun showSubtitles(subtitles: List<Stream>) {
        val active = subtitles.firstOrNull { it -> it.active }
        if (active != null) {
            Toast.makeText(activity, "Subtitles: ${active.toDisplayString()}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun showMute(state: MuteState) {
        toggleMute.background = when (state) {
            MuteState.ON -> activity.resources.getDrawable(R.drawable.round_button_orange, null)
            else -> activity.resources.getDrawable(R.drawable.round_button_gray, null)
        }
    }

    override fun onPlaybackStarted() {
        if (activity is PlaybackListener) (activity as PlaybackListener).onPlaybackStarted()
    }

    override fun onPlaybackStopped() {
        if (activity is PlaybackListener) (activity as PlaybackListener).onPlaybackStopped()
    }
}
