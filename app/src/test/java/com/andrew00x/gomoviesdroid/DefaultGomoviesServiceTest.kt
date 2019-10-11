package com.andrew00x.gomoviesdroid

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.Mockito.`when` as whenInvoke

@RunWith(MockitoJUnitRunner::class)
class DefaultGomoviesServiceTest {
  @Mock private lateinit var api: GomoviesApi
  @InjectMocks lateinit var underTest: DefaultGomoviesService

  @Test fun `list all movies`() {
    val movies = listOf(
        MovieData(1, "gladiator.mkv", "/movies/gladiator.mkv", "movies_1", true),
        MovieData(2, "brave heart.mkv", "/movies/brave heart.mkv", "movies_1", true)
    )
    whenInvoke(api.list()).thenReturn(Single.just(movies))
    assertThat(underTest.list().blockingGet()).isEqualTo(movies)
  }

  @Test fun `search movies`() {
    val movies = listOf(
        MovieData(2, "brave heart.mkv", "/movies/brave heart.mkv", "movies_1", true)
    )
    whenInvoke(api.search("brave")).thenReturn(Single.just(movies))
    assertThat(underTest.search("brave").blockingGet()).isEqualTo(movies)
  }

  @Test fun `loads movie details`() {
    val details = MovieDetailsData(originalTitle = "Brave Heart", tmdbId = 2)
    whenInvoke(api.details(1, "en")).thenReturn(Single.just(details))
    assertThat(underTest.details(1, "en").blockingGet()).isEqualTo(details)
  }

  @Test fun `searches for movie details`() {
    val details = listOf(MovieDetailsData(originalTitle = "Brave Heart", tmdbId = 2), MovieDetailsData(originalTitle = "Angel Heart", tmdbId = 3))
    whenInvoke(api.searchDetails("heart")).thenReturn(Single.just(details))
    assertThat(underTest.searchDetails("heart", "en").blockingGet()).isEqualTo(details)
  }

  @Test fun `play movie`() {
    val playback = Playback("/movies/brave heart.mkv")
    val status = PlayerStatus(file = "/movies/brave heart.mkv")
    whenInvoke(api.play(playback)).thenReturn(Single.just(status))
    assertThat(underTest.play(playback).blockingGet()).isEqualTo(status)
  }

  @Test fun play() {
    underTest.play()
    verify(api).play()
  }

  @Test fun pause() {
    underTest.pause()
    verify(api).pause()
  }

  @Test fun `play pause`() {
    underTest.playPause()
    verify(api).playPause()
  }

  @Test fun replay() {
    underTest.replay()
    verify(api).replay()
  }

  @Test fun seek() {
    underTest.seek(300)
    verify(api).seek(Position(300))
  }

  @Test fun `set position`() {
    underTest.setPosition(300)
    verify(api).setPosition(Position(300))
  }

  @Test fun `get status`() {
    val status = PlayerStatus(file = "/movies/brave heart.mkv")
    whenInvoke(api.getStatus()).thenReturn(Single.just(status))
    assertThat(underTest.getStatus().blockingGet()).isEqualTo(status)
  }

  @Test fun stop() {
    underTest.stop()
    verify(api).stop()
  }

  @Test fun `toggle mute`() {
    val status = PlayerStatus(file = "/movies/brave heart.mkv", muted = true)
    whenInvoke(api.toggleMute()).thenReturn(Single.just(status))
    assertThat(underTest.toggleMute().blockingGet()).isEqualTo(status)
  }

  @Test fun `toggle subtitles`() {
    val status = PlayerStatus(file = "/movies/brave heart.mkv", subtitlesOff = true)
    whenInvoke(api.toggleSubtitles()).thenReturn(Single.just(status))
    assertThat(underTest.toggleSubtitles().blockingGet()).isEqualTo(status)
  }

  @Test fun `volume up`() {
    val vol = Volume(1.3F)
    whenInvoke(api.volumeUp()).thenReturn(Single.just(vol))
    assertThat(underTest.volumeUp().blockingGet()).isEqualTo(vol)
  }

  @Test fun `volume down`() {
    val vol = Volume(0.7F)
    whenInvoke(api.volumeDown()).thenReturn(Single.just(vol))
    assertThat(underTest.volumeDown().blockingGet()).isEqualTo(vol)
  }

  @Test fun `next audio track`() {
    val audios = listOf(Stream(index = 1, lang = "ukr"), Stream(index = 2, lang = "en", active = true))
    whenInvoke(api.nextAudioTrack()).thenReturn(Single.just(audios))
    assertThat(underTest.nextAudioTrack().blockingGet()).isEqualTo(audios)
  }

  @Test fun `retrieve audio tracks`() {
    val audios = listOf(Stream(index = 1, lang = "ukr", active = true), Stream(index = 2, lang = "en"))
    whenInvoke(api.audioTracks()).thenReturn(Single.just(audios))
    assertThat(underTest.audioTracks().blockingGet()).isEqualTo(audios)
  }

  @Test fun `previous audio track`() {
    val audios = listOf(Stream(index = 1, lang = "ukr", active = true), Stream(index = 2, lang = "en"))
    whenInvoke(api.previousAudioTrack()).thenReturn(Single.just(audios))
    assertThat(underTest.previousAudioTrack().blockingGet()).isEqualTo(audios)
  }

  @Test fun `select audio track`() {
    val audios = listOf(Stream(index = 1, lang = "ukr", active = true), Stream(index = 2, lang = "en"))
    whenInvoke(api.selectAudioTrack(StreamIndex(2))).thenReturn(Single.just(audios))
    assertThat(underTest.selectAudioTrack(2).blockingGet()).isEqualTo(audios)
  }

  @Test fun `retrieve subtitles`() {
    val subs = listOf(Stream(index = 1, lang = "ukr", active = true), Stream(index = 2, lang = "en"))
    whenInvoke(api.subtitles()).thenReturn(Single.just(subs))
    assertThat(underTest.subtitles().blockingGet()).isEqualTo(subs)
  }

  @Test fun `next subtitle`() {
    val subs = listOf(Stream(index = 1, lang = "ukr"), Stream(index = 2, lang = "en", active = true))
    whenInvoke(api.nextSubtitle()).thenReturn(Single.just(subs))
    assertThat(underTest.nextSubtitle().blockingGet()).isEqualTo(subs)
  }

  @Test fun `previous subtitle`() {
    val subs = listOf(Stream(index = 1, lang = "ukr", active = true), Stream(index = 2, lang = "en"))
    whenInvoke(api.previousSubtitle()).thenReturn(Single.just(subs))
    assertThat(underTest.previousSubtitle().blockingGet()).isEqualTo(subs)
  }

  @Test fun `select subtitle`() {
    val subs = listOf(Stream(index = 1, lang = "ukr"), Stream(index = 2, lang = "en", active = true))
    whenInvoke(api.selectSubtitle(StreamIndex(2))).thenReturn(Single.just(subs))
    assertThat(underTest.selectSubtitle(2).blockingGet()).isEqualTo(subs)
  }

  @Test fun `enqueue movies`() {
    val movies = listOf("/movies/gladiator.mkv", "/movies/brave heart.mkv")
    whenInvoke(api.enqueue(listOf(MoviePath("/movies/gladiator.mkv"), MoviePath("/movies/brave heart.mkv"))))
        .thenReturn(Single.just(listOf(MoviePath("/movies/brave heart.mkv"))))
    assertThat(underTest.enqueue(movies).blockingGet()).isEqualTo(listOf("/movies/brave heart.mkv"))
  }

  @Test fun `retrieve queue`() {
    whenInvoke(api.queue())
        .thenReturn(Single.just(listOf(MoviePath("/movies/gladiator.mkv"), MoviePath("/movies/brave heart.mkv"))))
    assertThat(underTest.queue().blockingGet()).isEqualTo(listOf("/movies/gladiator.mkv", "/movies/brave heart.mkv"))
  }

  @Test fun `remove movie from queue`() {
    whenInvoke(api.dequeue(Position(1))).thenReturn(Single.just(listOf(MoviePath("/movies/gladiator.mkv"))))
    assertThat(underTest.dequeue(1).blockingGet()).isEqualTo(listOf("/movies/gladiator.mkv"))
  }

  @Test fun `clear queue`() {
    whenInvoke(api.clearQueue()).thenReturn(Completable.complete())
    underTest.clearQueue()
    verify(api).clearQueue()
  }

  @Test fun `shift queue`() {
    whenInvoke(api.shiftQueue(Position(1))).thenReturn(Single.just(listOf(MoviePath("/movies/gladiator.mkv"))))
    assertThat(underTest.shiftQueue(1).blockingGet()).isEqualTo(listOf("/movies/gladiator.mkv"))
  }
}
