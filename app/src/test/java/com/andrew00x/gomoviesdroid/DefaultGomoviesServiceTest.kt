package com.andrew00x.gomoviesdroid

import assertk.assertThat
import assertk.assertions.isEqualTo
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
  @InjectMocks lateinit var service: DefaultGomoviesService

  @Test
  fun `list all movies`() {
    val movies = listOf(
        Movie(1, "gladiator.mkv", "/movies/gladiator.mkv", "movies_1", true),
        Movie(2, "brave heart.mkv", "/movies/brave heart.mkv", "movies_1", true)
    )
    whenInvoke(api.list()).thenReturn(Single.just(movies))
    assertThat(service.list().blockingGet()).isEqualTo(movies)
  }

  @Test
  fun `search movies`() {
    val movies = listOf(
        Movie(2, "brave heart.mkv", "/movies/brave heart.mkv", "movies_1", true)
    )
    whenInvoke(api.search("brave")).thenReturn(Single.just(movies))
    assertThat(service.search("brave").blockingGet()).isEqualTo(movies)
  }

  @Test
  fun `play movie`() {
    val playback = Playback("/movies/brave heart.mkv")
    val status = PlayerStatus(file = "/movies/brave heart.mkv")
    whenInvoke(api.play(playback)).thenReturn(Single.just(status))
    assertThat(service.play(playback).blockingGet()).isEqualTo(status)
  }

  @Test
  fun play() {
    service.play()
    verify(api).play()
  }

  @Test
  fun pause() {
    service.pause()
    verify(api).pause()
  }

  @Test
  fun replay() {
    service.replay()
    verify(api).replay()
  }

  @Test
  fun seek() {
    service.seek(300)
    verify(api).seek(Position(300))
  }

  @Test
  fun `set position`() {
    service.setPosition(300)
    verify(api).setPosition(Position(300))
  }

  @Test
  fun `get status`() {
    val status = PlayerStatus(file = "/movies/brave heart.mkv")
    whenInvoke(api.getStatus()).thenReturn(Single.just(status))
    assertThat(service.getStatus().blockingGet()).isEqualTo(status)
  }

  @Test
  fun stop() {
    service.stop()
    verify(api).stop()
  }

  @Test
  fun `toggle mute`() {
    val status = PlayerStatus(file = "/movies/brave heart.mkv", muted = true)
    whenInvoke(api.toggleMute()).thenReturn(Single.just(status))
    assertThat(service.toggleMute().blockingGet()).isEqualTo(status)
  }

  @Test
  fun `toggle subtitles`() {
    val status = PlayerStatus(file = "/movies/brave heart.mkv", subtitlesOff = true)
    whenInvoke(api.toggleSubtitles()).thenReturn(Single.just(status))
    assertThat(service.toggleSubtitles().blockingGet()).isEqualTo(status)
  }

  @Test
  fun `volume up`() {
    val vol = Volume(1.3F)
    whenInvoke(api.volumeUp()).thenReturn(Single.just(vol))
    assertThat(service.volumeUp().blockingGet()).isEqualTo(vol)
  }

  @Test
  fun `volume down`() {
    val vol = Volume(0.7F)
    whenInvoke(api.volumeDown()).thenReturn(Single.just(vol))
    assertThat(service.volumeDown().blockingGet()).isEqualTo(vol)
  }

  @Test
  fun `next audio track`() {
    val audios = listOf(Stream(index = 1, lang = "ukr"), Stream(index = 2, lang = "en", active = true))
    whenInvoke(api.nextAudioTrack()).thenReturn(Single.just(audios))
    assertThat(service.nextAudioTrack().blockingGet()).isEqualTo(audios)
  }

  @Test
  fun `previous audio track`() {
    val audios = listOf(Stream(index = 1, lang = "ukr", active = true), Stream(index = 2, lang = "en"))
    whenInvoke(api.previousAudioTrack()).thenReturn(Single.just(audios))
    assertThat(service.previousAudioTrack().blockingGet()).isEqualTo(audios)
  }

  @Test
  fun `select audio track`() {
    val audios = listOf(Stream(index = 1, lang = "ukr", active = true), Stream(index = 2, lang = "en"))
    whenInvoke(api.selectAudioTrack(StreamIndex(2))).thenReturn(Single.just(audios))
    assertThat(service.selectAudioTrack(2).blockingGet()).isEqualTo(audios)
  }

  @Test
  fun `next subtitle`() {
    val subs = listOf(Stream(index = 1, lang = "ukr"), Stream(index = 2, lang = "en", active = true))
    whenInvoke(api.nextSubtitle()).thenReturn(Single.just(subs))
    assertThat(service.nextSubtitle().blockingGet()).isEqualTo(subs)
  }

  @Test
  fun `previous subtitle`() {
    val subs = listOf(Stream(index = 1, lang = "ukr", active = true), Stream(index = 2, lang = "en"))
    whenInvoke(api.previousSubtitle()).thenReturn(Single.just(subs))
    assertThat(service.previousSubtitle().blockingGet()).isEqualTo(subs)
  }

  @Test
  fun `select subtitle`() {
    val subs = listOf(Stream(index = 1, lang = "ukr"), Stream(index = 2, lang = "en", active = true))
    whenInvoke(api.selectSubtitle(StreamIndex(2))).thenReturn(Single.just(subs))
    assertThat(service.selectSubtitle(2).blockingGet()).isEqualTo(subs)
  }
}