package com.andrew00x.gomoviesdroid

import io.reactivex.Single
import retrofit2.http.*

data class Movie(
    val id: Int,
    val title: String,
    val file: String,
    val driveName: String,
    val available: Boolean
)

data class PlayerStatus(val file: String,
                        val duration: Int = 0,
                        val position: Int = 0,
                        val paused: Boolean = false,
                        val muted: Boolean = false,
                        val subtitlesOff: Boolean = false,
                        val activeAudioTrack: Int = -1,
                        val activeSubtitle: Int = -1,
                        val stopped: Boolean = false
)

data class Position(val position: Int)

data class Stream(
    val index: Int,
    val lang: String = "",
    val name: String = "",
    val codec: String = "",
    val active: Boolean = false) {
  fun toDisplayString(): String {
    return when {
      name.isNotBlank() -> name
      lang.isNotBlank() -> lang
      else -> index.toString()
    }
  }
}

data class StreamIndex(val index: Int)

data class Volume(val volume: Float) {
  fun toDisplayString(): String {
    // https://github.com/popcornmix/omxplayer#volume-rw
    val dB = 20.0 * Math.log10(volume.toDouble())
    return "Volume: %.1fdB".format(dB)
  }
}

data class Playback(
    val file: String,
    val position: Int = 0,
    val activeAudioTrack: Int = -1,
    val activeSubtitle: Int = -1
)

data class MoviePath(val file: String)

interface GomoviesApi {
  @GET("list")
  fun list(): Single<List<Movie>>

  @POST("search")
  fun search(@Query("q") title: String): Single<List<Movie>>

  @POST("play")
  fun play(@Body playback: Playback): Single<PlayerStatus>

  fun enqueue(@Body movie: MoviePath)

  @POST("player/play")
  fun play(): Single<PlayerStatus>

  @POST("player/replay")
  fun replay(): Single<PlayerStatus>

  @POST("player/pause")
  fun pause(): Single<PlayerStatus>

  @POST("player/playpause")
  fun playPause(): Single<PlayerStatus>

  @POST("player/stop")
  fun stop(): Single<PlayerStatus>

  @GET("player/status")
  fun getStatus(): Single<PlayerStatus>

  @POST("player/seek")
  fun seek(@Body position: Position): Single<PlayerStatus>

  @POST("player/position")
  fun setPosition(@Body position: Position): Single<PlayerStatus>

  @POST("player/togglemute")
  fun toggleMute(): Single<PlayerStatus>

  @POST("player/togglesubtitles")
  fun toggleSubtitles(): Single<PlayerStatus>

  @GET("player/audios")
  fun audioTracks(): Single<List<Stream>>

  @POST("player/nextaudiotrack")
  fun nextAudioTrack(): Single<List<Stream>>

  @POST("player/previousaudiotrack")
  fun previousAudioTrack(): Single<List<Stream>>

  @POST("player/audio")
  fun selectAudioTrack(@Body index: StreamIndex): Single<List<Stream>>

  @GET("player/subtitles")
  fun subtitles(): Single<List<Stream>>

  @POST("player/nextsubtitle")
  fun nextSubtitle(): Single<List<Stream>>

  @POST("player/previoussubtitle")
  fun previousSubtitle(): Single<List<Stream>>

  @POST("player/subtitle")
  fun selectSubtitle(@Body index: StreamIndex): Single<List<Stream>>

  @POST("player/volumedown")
  fun volumeDown(): Single<Volume>

  @POST("player/volumeup")
  fun volumeUp(): Single<Volume>
}

