package com.andrew00x.gomoviesdroid

import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.*
import kotlin.math.log10

data class MovieData(
    val id: Int = -1,
    val title: String = "",
    val file: String = "",
    val drive: String = "",
    val available: Boolean = false,
    val detailsAvailable: Boolean = false,
    val details: MovieDetailsData? = null
)

data class MovieDetailsData(
  val budget: Long = 0,
  val companies: List<String> = listOf(),
  val countries: List<String> = listOf(),
  val genres: List<String> = listOf(),
  val originalTitle: String = "",
  val overview: String = "",
  val posterSmallUrl: String = "",
  val posterLargeUrl: String = "",
  val releaseDate: String = "",
  val revenue: Long = 0,
  val runtime: Int = 0,
  val tagline: String = "",
  val tmdbId: Int = -1
)

data class PlayerStatus(val file: String = "",
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
    val index: Int = -1,
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
    val dB = 20.0 * log10(volume.toDouble())
    return "Volume: %.1fdB".format(dB)
  }
}

data class Playback(
    val file: String = "",
    val position: Int = 0,
    val activeAudioTrack: Int = -1,
    val activeSubtitle: Int = -1
)

data class MoviePath(val file: String)

data class TorrentDownload(
    val name: String = "",
    val path: String = "",
    val size: Long = 0,
    val completedSize: Long = 0,
    val completed: Boolean = false,
    val stopped: Boolean = false,
    val ratio: Float = 0.0f,
    val attrs: Map<String, String> = emptyMap()
)

data class TorrentFile(
    val file: String // base64 encoded content of torrent file
)

interface GomoviesApi {
  @GET("list")
  fun list(@Query("details") details: Boolean = false): Single<List<MovieData>>

  @GET("search")
  fun search(@Query("q") title: String, @Query("details") details: Boolean = false): Single<List<MovieData>>

  @GET("details")
  fun details(@Query("id") id: Int, @Query("lang") lang: String): Single<MovieDetailsData>

  @GET("details/search")
  fun searchDetails(@Query("q") title: String): Single<List<MovieDetailsData>>

  @POST("play")
  fun play(@Body playback: Playback): Single<PlayerStatus>

  @POST("enqueue")
  fun enqueue(@Body movies: List<MoviePath>): Single<List<MoviePath>>

  @GET("queue")
  fun queue(): Single<List<MoviePath>>

  @POST("dequeue")
  fun dequeue(@Body position: Position): Single<List<MoviePath>>

  @POST("clearqueue")
  fun clearQueue(): Completable

  @POST("shiftqueue")
  fun shiftQueue(@Body position: Position): Single<List<MoviePath>>

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

  @POST("torrent/add")
  fun addTorrent(@Body torrent: TorrentFile): Completable

  @GET("torrent/list")
  fun listTorrents(): Single<List<TorrentDownload>>

  @POST("torrent/start")
  fun startDownload(@Body torrent: TorrentDownload): Single<TorrentDownload>

  @POST("torrent/stop")
  fun stopDownload(@Body torrent: TorrentDownload): Single<TorrentDownload>

  @POST("torrent/delete")
  fun deleteTorrent(@Body torrent: TorrentDownload): Single<List<TorrentDownload>>
}
