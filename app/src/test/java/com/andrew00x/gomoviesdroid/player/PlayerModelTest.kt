package com.andrew00x.gomoviesdroid.player

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isSameAs
import com.andrew00x.gomoviesdroid.*
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.Mockito.`when` as whenInvoke

@RunWith(MockitoJUnitRunner::class)
class PlayerModelTest {
  @Mock lateinit var service: GomoviesService
  @Mock lateinit var playbacks: PlaybackRepository
  @InjectMocks lateinit var model: PlayerModel

  @Test
  fun `play movie`() {
    val playback = Playback("/movies/brave heart.mkv")
    val status = PlayerStatus(file = "/movies/brave heart.mkv")
    whenInvoke(service.play(playback)).thenReturn(Single.just(status))
    assertThat(model.play("/movies/brave heart.mkv").blockingGet()).isEqualTo(status)
  }

  @Test
  fun `seek forward`() {
    val status = PlayerStatus(file = "/movies/brave heart.mkv")
    whenInvoke(service.seek(anyInt())).thenReturn(Single.just(status))

    val observer = TestObserver<PlayerStatus>()
    model.forward(600).subscribe(observer)

    verify(service).seek(600)
    assertThat(observer.values()[0]).isSameAs(status)
  }

  @Test
  fun rewind() {
    val status = PlayerStatus(file = "/movies/brave heart.mkv")
    whenInvoke(service.seek(anyInt())).thenReturn(Single.just(status))

    val observer = TestObserver<PlayerStatus>()
    model.rewind(600).subscribe(observer)

    verify(service).seek(-600)
    assertThat(observer.values()[0]).isSameAs(status)
  }

  @Test
  fun `switch to next subtitles`() {
    val audios = listOf<Stream>()
    whenInvoke(service.nextSubtitle()).thenReturn(Single.just(audios))

    val observer = TestObserver<List<Stream>>()
    model.switchToNextSubtitle().subscribe(observer)

    assertThat(observer.values()[0]).isSameAs(audios)
  }

  @Test
  fun `switch to previous audio track`() {
    val audios = listOf<Stream>()
    whenInvoke(service.previousAudioTrack()).thenReturn(Single.just(audios))

    val observer = TestObserver<List<Stream>>()
    model.switchToPreviousAudioTrack().subscribe(observer)

    assertThat(observer.values()[0]).isSameAs(audios)
  }

  @Test
  fun `switch to previous subtitle`() {
    val audios = listOf<Stream>()
    whenInvoke(service.previousSubtitle()).thenReturn(Single.just(audios))

    val observer = TestObserver<List<Stream>>()
    model.switchToPreviousSubtitle().subscribe(observer)

    assertThat(observer.values()[0]).isSameAs(audios)
  }

  @Test
  fun pause() {
    val status = PlayerStatus(file = "/movies/brave heart.mkv")
    whenInvoke(service.pause()).thenReturn(Single.just(status))

    val observer = TestObserver<PlayerStatus>()
    model.pause().subscribe(observer)

    assertThat(observer.values()[0]).isSameAs(status)
  }

  @Test
  fun play() {
    val status = PlayerStatus(file = "/movies/brave heart.mkv")
    whenInvoke(service.play()).thenReturn(Single.just(status))

    val observer = TestObserver<PlayerStatus>()
    model.play().subscribe(observer)

    assertThat(observer.values()[0]).isSameAs(status)
  }

  @Test
  fun replay() {
    val status = PlayerStatus(file = "/movies/brave heart.mkv")
    whenInvoke(service.replay()).thenReturn(Single.just(status))

    val observer = TestObserver<PlayerStatus>()
    model.replay().subscribe(observer)

    assertThat(observer.values()[0]).isSameAs(status)
  }

  @Test
  fun `cleanup playback on replay`() {
    val status = PlayerStatus(file = "/movies/brave heart.mkv")
    val playback = Playback("/movies/brave heart.mkv", 222, 1, 2)
    whenInvoke(service.replay()).thenReturn(Single.just(status))
    whenInvoke(playbacks.find("/movies/brave heart.mkv")).thenReturn(playback)

    val observer = TestObserver<PlayerStatus>()
    model.replay().subscribe(observer)

    verify(playbacks).remove(playback)
  }

  @Test
  fun `set position`() {
    val status = PlayerStatus(file = "/movies/brave heart.mkv")
    whenInvoke(service.setPosition(anyInt())).thenReturn(Single.just(status))

    val observer = TestObserver<PlayerStatus>()
    model.setPosition(123).subscribe(observer)

    verify(service).setPosition(123)
    assertThat(observer.values()[0]).isSameAs(status)
  }

  @Test
  fun `retrieve status`() {
    val status = PlayerStatus(file = "/movies/brave heart.mkv")
    whenInvoke(service.getStatus()).thenReturn(Single.just(status))

    val observer = TestObserver<PlayerStatus>()
    model.getStatus().subscribe(observer)

    assertThat(observer.values()[0]).isSameAs(status)
  }

  @Test
  fun `stop playback`() {
    val status = PlayerStatus(file = "/movies/brave heart.mkv", position = 111, activeAudioTrack = 1, activeSubtitle = 2)
    whenInvoke(service.stop()).thenReturn(Single.just(status))

    val observer = TestObserver<PlayerStatus>()
    model.stop().subscribe(observer)

    assertThat(observer.values()[0]).isSameAs(status)
  }

  @Test
  fun `save playback on stop`() {
    val status = PlayerStatus(file = "/movies/brave heart.mkv", position = 222, activeAudioTrack = 1, activeSubtitle = 2)
    val playback = Playback("/movies/brave heart.mkv", 222, 1, 2)
    val existent = Playback("/movies/brave heart.mkv", 111, 1, 2)
    whenInvoke(playbacks.find("/movies/brave heart.mkv")).thenReturn(existent)
    whenInvoke(service.stop()).thenReturn(Single.just(status))

    val observer = TestObserver<PlayerStatus>()
    model.stop().subscribe(observer)

    verify(playbacks).remove(existent)
    verify(playbacks).save(playback)
  }

  @Test
  fun `toggle mute`() {
    model.toggleMute()
    verify(service).toggleMute()
  }

  @Test
  fun `toggle subtitles`() {
    model.toggleSubtitles()
    verify(service).toggleSubtitles()
  }

  @Test
  fun `decrease volume`() {
    val vol = Volume(0.3f)
    whenInvoke(service.volumeDown()).thenReturn(Single.just(vol))

    val observer = TestObserver<Volume>()
    model.volumeDown().subscribe(observer)

    assertThat(observer.values()[0]).isSameAs(vol)
  }

  @Test
  fun `increase volume`() {
    val vol = Volume(0.3f)
    whenInvoke(service.volumeUp()).thenReturn(Single.just(vol))

    val observer = TestObserver<Volume>()
    model.volumeUp().subscribe(observer)

    assertThat(observer.values()[0]).isSameAs(vol)
  }
}
