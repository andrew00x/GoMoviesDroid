package com.andrew00x.gomoviesdroid.player

import com.activeandroid.Model
import com.activeandroid.annotation.Column
import com.activeandroid.annotation.Table
import com.activeandroid.query.Delete
import com.activeandroid.query.Select
import com.andrew00x.gomoviesdroid.*
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Table(name = "playback")
data class AAPlayback(
    @Column(name = "file", unique = true) var file: String = "",
    @Column(name = "position") var position: Int = 0,
    @Column(name = "audiostreamindex") var activeAudioTrack: Int = -1,
    @Column(name = "subtitleindex") var activeSubtitle: Int = -1
) : Model()

@Singleton
class PlayerModel @Inject constructor(private val service: GomoviesService) {

  fun play(file: String): Single<PlayerStatus> {
    return service.play(
        Select().from(AAPlayback::class.java).where("file = ?", file).executeSingle<AAPlayback>()
            ?.let { Playback(it.file, it.position, it.activeAudioTrack, it.activeSubtitle) }
            ?: Playback(file)
    )
  }

  fun pause(): Single<PlayerStatus> = service.pause()

  fun play(): Single<PlayerStatus> = service.play()

  fun replay(): Single<PlayerStatus> =
      service.replay().doAfterSuccess { status -> Delete().from(AAPlayback::class.java).where("file = ?", status.file).execute<AAPlayback>() }

  fun playPause(): Single<PlayerStatus> = service.playPause()

  fun forward(position: Int): Single<PlayerStatus> = service.seek(position)

  fun rewind(position: Int): Single<PlayerStatus> = service.seek(-position)

  fun stop(): Single<PlayerStatus> {
    return service.stop().doAfterSuccess { status ->
      val playback = Select().from(AAPlayback::class.java).where("file = ?", status.file).executeSingle()
          ?: AAPlayback(file = status.file)
      playback.position = status.position
      playback.activeAudioTrack = status.activeAudioTrack
      playback.activeSubtitle = status.activeSubtitle
      playback.save()
    }
  }

  fun setPosition(position: Int): Single<PlayerStatus> = service.setPosition(position)

  fun switchToNextAudioTrack(): Single<List<Stream>> = service.nextAudioTrack()

  fun switchToPreviousAudioTrack(): Single<List<Stream>> = service.previousAudioTrack()

  fun switchToNextSubtitle(): Single<List<Stream>> = service.nextSubtitle()

  fun switchToPreviousSubtitle(): Single<List<Stream>> = service.previousSubtitle()

  fun toggleSubtitles(): Single<PlayerStatus> = service.toggleSubtitles()

  fun getStatus(): Single<PlayerStatus> = service.getStatus()

  fun volumeUp(): Single<Volume> = service.volumeUp()

  fun volumeDown(): Single<Volume> = service.volumeDown()

  fun toggleMute(): Single<PlayerStatus> = service.toggleMute()
}
