package com.andrew00x.gomoviesdroid

import io.reactivex.Single

interface GomoviesService {
  fun list(): Single<List<Movie>>
  fun pause(): Single<PlayerStatus>
  fun play(): Single<PlayerStatus>
  fun playPause(): Single<PlayerStatus>
  fun replay(): Single<PlayerStatus>
  fun play(playback: Playback): Single<PlayerStatus>
  fun search(title: String): Single<List<Movie>>
  fun seek(position: Int): Single<PlayerStatus>
  fun setPosition(position: Int): Single<PlayerStatus>
  fun getStatus(): Single<PlayerStatus>
  fun stop(): Single<PlayerStatus>
  fun audioTracks(): Single<List<Stream>>
  fun nextAudioTrack(): Single<List<Stream>>
  fun previousAudioTrack(): Single<List<Stream>>
  fun selectAudioTrack(index: Int): Single<List<Stream>>
  fun subtitles(): Single<List<Stream>>
  fun nextSubtitle(): Single<List<Stream>>
  fun previousSubtitle(): Single<List<Stream>>
  fun selectSubtitle(index: Int): Single<List<Stream>>
  fun toggleMute(): Single<PlayerStatus>
  fun toggleSubtitles(): Single<PlayerStatus>
  fun volumeUp(): Single<Volume>
  fun volumeDown(): Single<Volume>
}

class DefaultGomoviesService(private val api: GomoviesApi) : GomoviesService {
  override fun list(): Single<List<Movie>> {
    return api.list()
  }

  override fun search(title: String): Single<List<Movie>> {
    return api.search(title)
  }

  override fun play(playback: Playback): Single<PlayerStatus> {
    return api.play(playback)
  }

  override fun pause(): Single<PlayerStatus> {
    return api.pause()
  }

  override fun playPause(): Single<PlayerStatus> {
    return api.playPause()
  }

  override fun play(): Single<PlayerStatus> {
    return api.play()
  }

  override fun replay(): Single<PlayerStatus> {
    return api.replay()
  }

  override fun seek(position: Int): Single<PlayerStatus> {
    return api.seek(Position(position))
  }

  override fun setPosition(position: Int): Single<PlayerStatus> {
    return api.setPosition(Position(position))
  }

  override fun getStatus(): Single<PlayerStatus> {
    return api.getStatus()
  }

  override fun stop(): Single<PlayerStatus> {
    return api.stop()
  }

  override fun toggleMute(): Single<PlayerStatus> {
    return api.toggleMute()
  }

  override fun toggleSubtitles(): Single<PlayerStatus> {
    return api.toggleSubtitles()
  }

  override fun volumeDown(): Single<Volume> {
    return api.volumeDown()
  }

  override fun volumeUp(): Single<Volume> {
    return api.volumeUp()
  }

  override fun audioTracks(): Single<List<Stream>> {
    return api.audioTracks()
  }

  override fun nextAudioTrack(): Single<List<Stream>> {
    return api.nextAudioTrack()
  }

  override fun previousAudioTrack(): Single<List<Stream>> {
    return api.previousAudioTrack()
  }

  override fun selectAudioTrack(index: Int): Single<List<Stream>> {
    return api.selectAudioTrack(StreamIndex(index))
  }

  override fun subtitles(): Single<List<Stream>> {
    return api.subtitles()
  }

  override fun nextSubtitle(): Single<List<Stream>> {
    return api.nextSubtitle()
  }

  override fun previousSubtitle(): Single<List<Stream>> {
    return api.previousSubtitle()
  }

  override fun selectSubtitle(index: Int): Single<List<Stream>> {
    return api.selectSubtitle(StreamIndex(index))
  }
}
