package com.andrew00x.gomoviesdroid.player

import com.andrew00x.gomoviesdroid.*
import io.reactivex.Single
import javax.inject.Inject

class PlayerModel @Inject constructor(
    private val service: GomoviesService,
    private val playbacks: PlaybackRepository) {

  fun play(file: String): Single<PlayerStatus> {
    val playback = playbacks.find(file) ?: Playback(file)
    return service.play(playback)
  }

  fun pause(): Single<PlayerStatus> {
    return service.pause()
  }

  fun play(): Single<PlayerStatus> {
    return service.play()
  }

  fun replay(): Single<PlayerStatus> {
    return service.replay().doAfterSuccess { status ->
      val playback = playbacks.find(status.file)
      if (playback != null) playbacks.remove(playback)
    }
  }

  fun playPause(): Single<PlayerStatus> {
    return service.playPause()
  }

  fun forward(position: Int): Single<PlayerStatus> {
    return service.seek(position)
  }

  fun rewind(position: Int): Single<PlayerStatus> {
    return service.seek(-position)
  }

  fun stop(): Single<PlayerStatus> {
    return service.stop().doAfterSuccess { status ->
      val playback = Playback(file = status.file, position = status.position, activeAudioTrack = status.activeAudioTrack, activeSubtitle = status.activeSubtitle)
      val existent = playbacks.find(playback.file)
      if (existent != playback) {
        if (existent != null) {
          playbacks.remove(existent)
        }
        playbacks.save(playback)
      }
    }
  }

  fun setPosition(position: Int): Single<PlayerStatus> {
    return service.setPosition(position)
  }

  fun switchToNextAudioTrack(): Single<List<Stream>> {
    return service.nextAudioTrack()
  }

  fun switchToPreviousAudioTrack(): Single<List<Stream>> {
    return service.previousAudioTrack()
  }

  fun switchToNextSubtitle(): Single<List<Stream>> {
    return service.nextSubtitle()
  }

  fun switchToPreviousSubtitle(): Single<List<Stream>> {
    return service.previousSubtitle()
  }

  fun toggleSubtitles(): Single<PlayerStatus> {
    return service.toggleSubtitles()
  }

  fun getStatus(): Single<PlayerStatus> {
    return service.getStatus()
  }

  fun volumeUp(): Single<Volume> {
    return service.volumeUp()
  }

  fun volumeDown(): Single<Volume> {
    return service.volumeDown()
  }

  fun toggleMute(): Single<PlayerStatus> {
    return service.toggleMute()
  }
}

