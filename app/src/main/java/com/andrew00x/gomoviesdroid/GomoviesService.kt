package com.andrew00x.gomoviesdroid

import io.reactivex.Single

interface GomoviesService {
  fun list(): Single<List<MovieData>>
  fun search(title: String): Single<List<MovieData>>
  fun details(id: Int, lang: String): Single<MovieDetailsData>
  fun searchDetails(title: String, lang: String): Single<List<MovieDetailsData>>
  fun pause(): Single<PlayerStatus>
  fun play(): Single<PlayerStatus>
  fun playPause(): Single<PlayerStatus>
  fun replay(): Single<PlayerStatus>
  fun play(playback: Playback): Single<PlayerStatus>
  fun enqueue(paths: List<String>): Single<List<String>>
  fun queue(): Single<List<String>>
  fun dequeue(position: Int): Single<List<String>>
  fun clearQueue(): Single<Unit>
  fun shiftQueue(position: Int): Single<List<String>>
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
  override fun list(): Single<List<MovieData>> = api.list()

  override fun search(title: String): Single<List<MovieData>> = api.search(title)

  override fun details(id: Int, lang: String): Single<MovieDetailsData> = api.details(id, lang)

  override fun searchDetails(title: String, lang: String): Single<List<MovieDetailsData>> = api.searchDetails(title)

  override fun play(playback: Playback): Single<PlayerStatus> = api.play(playback)

  override fun enqueue(paths: List<String>): Single<List<String>> =
      api.enqueue(paths.map { MoviePath(it) }).map { queue -> queue.map { it.file } }

  override fun queue(): Single<List<String>> = api.queue().map { queue -> queue.map { it.file } }

  override fun dequeue(position: Int): Single<List<String>> =
      api.dequeue(Position(position)).map { queue -> queue.map { it.file } }

  override fun clearQueue(): Single<Unit> = api.clearQueue().toSingle { Unit }

  override fun shiftQueue(position: Int): Single<List<String>> =
      api.shiftQueue(Position(position)).map { queue -> queue.map { it.file } }

  override fun pause(): Single<PlayerStatus> = api.pause()

  override fun playPause(): Single<PlayerStatus> = api.playPause()

  override fun play(): Single<PlayerStatus> = api.play()

  override fun replay(): Single<PlayerStatus> = api.replay()

  override fun seek(position: Int): Single<PlayerStatus> = api.seek(Position(position))

  override fun setPosition(position: Int): Single<PlayerStatus> =
      api.setPosition(Position(position))

  override fun getStatus(): Single<PlayerStatus> = api.getStatus()

  override fun stop(): Single<PlayerStatus> = api.stop()

  override fun toggleMute(): Single<PlayerStatus> = api.toggleMute()

  override fun toggleSubtitles(): Single<PlayerStatus> = api.toggleSubtitles()

  override fun volumeDown(): Single<Volume> = api.volumeDown()

  override fun volumeUp(): Single<Volume> = api.volumeUp()

  override fun audioTracks(): Single<List<Stream>> = api.audioTracks()

  override fun nextAudioTrack(): Single<List<Stream>> = api.nextAudioTrack()

  override fun previousAudioTrack(): Single<List<Stream>> = api.previousAudioTrack()

  override fun selectAudioTrack(index: Int): Single<List<Stream>> =
      api.selectAudioTrack(StreamIndex(index))

  override fun subtitles(): Single<List<Stream>> = api.subtitles()

  override fun nextSubtitle(): Single<List<Stream>> = api.nextSubtitle()

  override fun previousSubtitle(): Single<List<Stream>> = api.previousSubtitle()

  override fun selectSubtitle(index: Int): Single<List<Stream>> =
      api.selectSubtitle(StreamIndex(index))
}
