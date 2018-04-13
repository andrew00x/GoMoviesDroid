package com.andrew00x.gomoviesdroid

import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

data class Movie(
        val id: Int,
        val title: String,
        val path: String,
        val driveName: String,
        val available: Boolean
)

data class MoviePath(val movie: String)

data class PlayerStatus(val playing: String = "",
                        val duration: Int = 0,
                        val position: Int = 0,
                        val paused: Boolean = false
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

data class Volume(val volume: Float)

interface GomoviesApi {
    @GET
    fun list(@Url url: String): Observable<List<Movie>>

    @POST
    fun mute(@Url url: String): Observable<Response<Void>>

    @POST
    fun nextAudioTrack(@Url url: String): Observable<List<Stream>>

    @POST
    fun nextSubtitles(@Url url: String): Observable<List<Stream>>

    @POST
    fun pause(@Url url: String): Observable<PlayerStatus>

    @POST
    fun play(@Url url: String): Observable<PlayerStatus>

    @POST
    fun play(@Url url: String, @Body moviePath: MoviePath): Observable<PlayerStatus>

    @POST
    fun previousAudioTrack(@Url url: String): Observable<List<Stream>>

    @POST
    fun previousSubtitles(@Url url: String): Observable<List<Stream>>

    @POST
    fun search(@Url url: String, @Query("q") title: String): Observable<List<Movie>>

    @POST
    fun seek(@Url url: String, @Body position: Position): Observable<PlayerStatus>

    @POST
    fun setPosition(@Url url: String, @Body position: Position): Observable<PlayerStatus>

    @GET
    fun status(@Url url: String): Observable<PlayerStatus>

    @POST
    fun stop(@Url url: String): Observable<PlayerStatus>

    @POST
    fun toggleSubtitles(@Url url: String): Observable<Response<Void>>

    @POST
    fun unmute(@Url url: String): Observable<Response<Void>>

    @POST
    fun volumeDown(@Url url: String): Observable<Volume>

    @POST
    fun volumeUp(@Url url: String): Observable<Volume>
}

