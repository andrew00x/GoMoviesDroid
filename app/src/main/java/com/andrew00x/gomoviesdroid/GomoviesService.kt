package com.andrew00x.gomoviesdroid

import com.andrew00x.gomoviesdroid.config.Configuration
import com.andrew00x.gomoviesdroid.config.ConfigurationService
import com.andrew00x.gomoviesdroid.config.ConfigurationUpdateListener
import io.reactivex.Observable
import retrofit2.Response

interface GomoviesService {
    fun list(): Observable<List<Movie>>
    fun mute(): Observable<Response<Void>>
    fun nextAudioTrack(): Observable<List<Stream>>
    fun nextSubtitles(): Observable<List<Stream>>
    fun pause(): Observable<PlayerStatus>
    fun play(): Observable<PlayerStatus>
    fun play(movie: Movie): Observable<PlayerStatus>
    fun previousAudioTrack(): Observable<List<Stream>>
    fun previousSubtitles(): Observable<List<Stream>>
    fun search(title: String): Observable<List<Movie>>
    fun seek(position: Int): Observable<PlayerStatus>
    fun setPosition(position: Int): Observable<PlayerStatus>
    fun status(): Observable<PlayerStatus>
    fun stop(): Observable<PlayerStatus>
    fun toggleSubtitles(): Observable<Response<Void>>
    fun unmute(): Observable<Response<Void>>
    fun volumeUp(): Observable<Volume>
    fun volumeDown(): Observable<Volume>
}

class DefaultGomoviesService (private val api: GomoviesApi, configService: ConfigurationService) : ConfigurationUpdateListener, GomoviesService {
    private var url: String

    init {
        this.url = configService.retrieve().serverUrl
        configService.addListener(this)
    }

    override fun onConfigurationUpdate(updated: Configuration) {
        url = updated.serverUrl
    }

    override fun list(): Observable<List<Movie>> {
        return api.list("${url}list")
    }

    override fun mute(): Observable<Response<Void>> {
        return api.mute("${url}player/mute")
    }

    override fun nextAudioTrack(): Observable<List<Stream>> {
        return api.nextAudioTrack("${url}player/nextaudiotrack")
    }

    override fun nextSubtitles(): Observable<List<Stream>> {
        return api.nextSubtitles("${url}player/nextsubtitles")
    }

    override fun pause(): Observable<PlayerStatus> {
        return api.pause("${url}player/pause")
    }

    override fun play(): Observable<PlayerStatus> {
        return api.play("${url}player/play")
    }

    override fun play(movie: Movie): Observable<PlayerStatus> {
        return api.play("${url}play", MoviePath(movie.path))
    }

    override fun previousAudioTrack(): Observable<List<Stream>> {
        return api.previousAudioTrack("${url}player/previousaudiotrack")
    }

    override fun previousSubtitles(): Observable<List<Stream>> {
        return api.previousSubtitles("${url}player/previoussubtitles")
    }

    override fun search(title: String): Observable<List<Movie>> {
        return api.search("${url}search", title)
    }

    override fun seek(position: Int): Observable<PlayerStatus> {
        return api.seek("${url}player/seek", Position(position))
    }

    override fun setPosition(position: Int): Observable<PlayerStatus> {
        return api.setPosition("${url}player/position", Position(position))
    }

    override fun status(): Observable<PlayerStatus> {
        return api.status("${url}player/status")
    }

    override fun stop(): Observable<PlayerStatus> {
        return api.stop("${url}player/stop")
    }

    override fun toggleSubtitles(): Observable<Response<Void>> {
        return api.toggleSubtitles("${url}player/togglesubtitles")
    }

    override fun unmute(): Observable<Response<Void>> {
        return api.unmute("${url}player/unmute")
    }

    override fun volumeDown(): Observable<Volume> {
        return api.volumeDown("${url}player/volumedown")
    }

    override fun volumeUp(): Observable<Volume> {
        return api.volumeUp("${url}player/volumeup")
    }
}
