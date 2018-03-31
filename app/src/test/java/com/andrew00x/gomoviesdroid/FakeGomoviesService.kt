package com.andrew00x.gomoviesdroid

import io.reactivex.Observable
import retrofit2.Response

class FakeGomoviesService : GomoviesService {
    override fun list(): Observable<List<Movie>> {
        return Observable.just(listOf(Movie(1, "Brave heart.mkv", "/Brave heart.mkv", "", true)))
    }

    override fun mute(): Observable<Response<Void>> {
        return Observable.just(Response.success(null as Void?))
    }

    override fun nextAudioTrack(): Observable<List<Stream>> {
        return Observable.just(listOf(Stream(index = 1, lang = "ukr", active = true)))
    }

    override fun nextSubtitles(): Observable<List<Stream>> {
        return Observable.just(listOf(Stream(index = 1, lang = "ukr", active = true)))
    }

    override fun pause(): Observable<PlayerStatus> {
        return Observable.just(PlayerStatus(playing = "/movies/Brave heart.mkv", paused = true))
    }

    override fun play(): Observable<PlayerStatus> {
        return Observable.just(PlayerStatus(playing = "/movies/Brave heart.mkv"))
    }

    override fun play(movie: Movie): Observable<PlayerStatus> {
        return Observable.just(PlayerStatus(playing = movie.path))
    }

    override fun previousAudioTrack(): Observable<List<Stream>> {
        return Observable.just(listOf(Stream(index = 2, lang = "en", active = true)))
    }

    override fun previousSubtitles(): Observable<List<Stream>> {
        return Observable.just(listOf(Stream(index = 2, lang = "en", active = true)))
    }

    override fun search(title: String): Observable<List<Movie>> {
        return Observable.just(listOf(Movie(1, "Brave heart.mkv", "/Brave heart.mkv", "", true)))
    }

    override fun seek(position: Int): Observable<PlayerStatus> {
        return Observable.just(PlayerStatus(playing = "/movies/Brave heart.mkv", position = 30 * 60, duration = 150 * 60))
    }

    override fun setPosition(position: Int): Observable<PlayerStatus> {
        return Observable.just(PlayerStatus(playing = "/movies/Brave heart.mkv", position = 30 * 90, duration = 150 * 60))
    }

    override fun status(): Observable<PlayerStatus> {
        return Observable.just(PlayerStatus(playing = "/movies/Brave heart.mkv"))
    }

    override fun stop(): Observable<PlayerStatus> {
        return Observable.just(PlayerStatus())
    }

    override fun toggleSubtitles(): Observable<Response<Void>> {
        return Observable.just(Response.success(null as Void?))
    }

    override fun unmute(): Observable<Response<Void>> {
        return Observable.just(Response.success(null as Void?))
    }

    override fun volumeDown(): Observable<Volume> {
        return Observable.just(Volume(1.3F))
    }

    override fun volumeUp(): Observable<Volume> {
        return Observable.just(Volume(1.7F))
    }
}