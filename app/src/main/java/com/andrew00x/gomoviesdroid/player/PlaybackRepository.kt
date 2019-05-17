package com.andrew00x.gomoviesdroid.player

import com.activeandroid.Model
import com.activeandroid.annotation.Column
import com.activeandroid.annotation.Table
import com.activeandroid.query.Delete
import com.activeandroid.query.Select
import com.andrew00x.gomoviesdroid.Playback

interface PlaybackRepository {
  fun find(file: String): Playback?
  fun save(playback: Playback)
  fun remove(playback: Playback)
  fun clear()
}

@Table(name = "playback")
data class AAPlayback(
    @Column(name = "file", unique = true) var file: String = "",
    @Column(name = "position") var position: Int = 0,
    @Column(name = "audiostreamindex") var audioStreamIndex: Int = -1,
    @Column(name = "subtitleindex") var subtitleIndex: Int = -1
) : Model()

class AAPlaybackRepository : PlaybackRepository {
  override fun find(file: String): Playback? {
    val data = Select().from(AAPlayback::class.java).where("file = ?", file).executeSingle<AAPlayback>()
    return if (data == null) null else Playback(data.file, data.position, data.audioStreamIndex, data.subtitleIndex)
  }

  override fun save(playback: Playback) {
    val data = Select().from(AAPlayback::class.java).where("file = ?", playback.file).executeSingle()
        ?: AAPlayback(playback.file)
    data.position = playback.position
    data.audioStreamIndex = playback.activeAudioTrack
    data.subtitleIndex = playback.activeSubtitle
    data.save()
  }

  override fun remove(playback: Playback) {
    Delete().from(AAPlayback::class.java).where("file = ?", playback.file).execute<AAPlayback>()
  }

  override fun clear() {
    Delete().from(AAPlayback::class.java).execute<AAPlayback>()
  }
}