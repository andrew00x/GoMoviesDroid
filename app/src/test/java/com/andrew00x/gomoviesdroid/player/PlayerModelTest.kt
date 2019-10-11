package com.andrew00x.gomoviesdroid.player

import assertk.assertThat
import assertk.assertions.*
import com.activeandroid.query.Select
import com.andrew00x.gomoviesdroid.*
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.mockito.Mockito.`when` as whenInvoke

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26], application = TestApplication::class, manifest = Config.NONE)
class PlayerModelTest {
  private lateinit var service: GomoviesService
  private lateinit var underTest: PlayerModel

  @Before fun setup() {
    service = mock(GomoviesService::class.java)
    whenInvoke(service.clearQueue()).thenReturn(Single.just(Unit))
    underTest = PlayerModel(service)
  }

  @Test fun `plays movie`() {
    val playback = Playback("/movies/brave heart.mkv")
    val status = PlayerStatus(file = "/movies/brave heart.mkv")
    whenInvoke(service.play(playback)).thenReturn(Single.just(status))
    assertThat(underTest.play("/movies/brave heart.mkv").blockingGet()).isEqualTo(status)
  }

  @Test fun `plays movie starting from existing playback`() {
    val playbackData = AAPlayback(file  = "/movies/brave heart.mkv", position = 123, activeAudioTrack = 1, activeSubtitle = 2)
    playbackData.save()
    val playback = Playback(file  = "/movies/brave heart.mkv", position = 123, activeAudioTrack = 1, activeSubtitle = 2)
    val status = PlayerStatus(file = "/movies/brave heart.mkv")
    whenInvoke(service.play(playback)).thenReturn(Single.just(status))
    assertThat(underTest.play("/movies/brave heart.mkv").blockingGet()).isEqualTo(status)
  }

  @Test fun `seeks forward`() {
    val status = PlayerStatus(file = "/movies/brave heart.mkv")
    whenInvoke(service.seek(600)).thenReturn(Single.just(status))
    assertThat(underTest.forward(600).blockingGet()).isEqualTo(status)
  }

  @Test fun rewinds() {
    val status = PlayerStatus(file = "/movies/brave heart.mkv")
    whenInvoke(service.seek(-600)).thenReturn(Single.just(status))
    assertThat(underTest.rewind(600).blockingGet()).isEqualTo(status)
  }

  @Test fun `switches to next subtitles`() {
    val subs = listOf(Stream(index = 1, lang = "ukr"), Stream(index = 2, lang = "en", active = true))
    whenInvoke(service.nextSubtitle()).thenReturn(Single.just(subs))
    assertThat(underTest.switchToNextSubtitle().blockingGet()).isEqualTo(subs)
  }

  @Test fun `switches to previous subtitle`() {
    val subs = listOf(Stream(index = 1, lang = "ukr", active = true), Stream(index = 2, lang = "en"))
    whenInvoke(service.previousSubtitle()).thenReturn(Single.just(subs))
    assertThat(underTest.switchToPreviousSubtitle().blockingGet()).isEqualTo(subs)
  }

  @Test fun `switches to next audio track`() {
    val audios = listOf(Stream(index = 1, lang = "ukr"), Stream(index = 2, lang = "en", active = true))
    whenInvoke(service.nextAudioTrack()).thenReturn(Single.just(audios))
    assertThat(underTest.switchToNextAudioTrack().blockingGet()).isEqualTo(audios)
  }

  @Test fun `switches to previous audio track`() {
    val audios = listOf(Stream(index = 1, lang = "ukr", active = true), Stream(index = 2, lang = "en"))
    whenInvoke(service.previousAudioTrack()).thenReturn(Single.just(audios))
    assertThat(underTest.switchToPreviousAudioTrack().blockingGet()).isEqualTo(audios)
  }

  @Test fun pauses() {
    val status = PlayerStatus(file = "/movies/brave heart.mkv")
    whenInvoke(service.pause()).thenReturn(Single.just(status))
    assertThat(underTest.pause().blockingGet()).isEqualTo(status)
  }

  @Test fun plays() {
    val status = PlayerStatus(file = "/movies/brave heart.mkv")
    whenInvoke(service.play()).thenReturn(Single.just(status))
    assertThat(underTest.play().blockingGet()).isEqualTo(status)
  }

  @Test fun `plays pause`() {
    val status = PlayerStatus(file = "/movies/brave heart.mkv")
    whenInvoke(service.playPause()).thenReturn(Single.just(status))
    assertThat(underTest.playPause().blockingGet()).isEqualTo(status)
  }

  @Test fun replays() {
    val playbackData = AAPlayback(file  = "/movies/brave heart.mkv", position = 123, activeAudioTrack = 1, activeSubtitle = 2)
    playbackData.save()
    val status = PlayerStatus(file = "/movies/brave heart.mkv")
    whenInvoke(service.replay()).thenReturn(Single.just(status))

    assertThat(Select().from(AAPlayback::class.java).where("file = ?", "/movies/brave heart.mkv").count()).isEqualTo(1)
    assertThat(underTest.replay().blockingGet()).isEqualTo(status)
    assertThat(Select().from(AAPlayback::class.java).where("file = ?", "/movies/brave heart.mkv").count()).isZero()
  }

  @Test(expected = RuntimeException::class)
  fun `does not clean playback when error occurred while replay`() {
    val playbackData = AAPlayback(file  = "/movies/brave heart.mkv", position = 123, activeAudioTrack = 1, activeSubtitle = 2)
    playbackData.save()
    val error = RuntimeException()
    whenInvoke(service.replay()).thenThrow(error)

    assertThat(Select().from(AAPlayback::class.java).where("file = ?", "/movies/brave heart.mkv").count()).isEqualTo(1)
    try {
      underTest.replay().blockingGet()
    } catch (e: Exception) {
      assertThat(Select().from(AAPlayback::class.java).where("file = ?", "/movies/brave heart.mkv").count()).isEqualTo(1)
      throw e
    }
  }

  @Test fun `sets position`() {
    val status = PlayerStatus(file = "/movies/brave heart.mkv")
    whenInvoke(service.setPosition(123)).thenReturn(Single.just(status))
    assertThat(underTest.setPosition(123).blockingGet()).isEqualTo(status)
  }

  @Test fun `retrieves status`() {
    val status = PlayerStatus(file = "/movies/brave heart.mkv")
    whenInvoke(service.getStatus()).thenReturn(Single.just(status))
    assertThat(underTest.getStatus().blockingGet()).isEqualTo(status)
  }

  @Test fun `saves playback on stop`() {
    val status = PlayerStatus(file = "/movies/brave heart.mkv", position = 111, activeAudioTrack = 1, activeSubtitle = 2)
    whenInvoke(service.stop()).thenReturn(Single.just(status))
    assertThat(underTest.stop().blockingGet()).isEqualTo(status)
    assertThat(Select().from(AAPlayback::class.java).where("file = ?", status.file).execute<AAPlayback>())
        .containsOnly(AAPlayback(file = status.file, position = status.position, activeAudioTrack = status.activeAudioTrack, activeSubtitle = status.activeSubtitle))
  }

  @Test fun `overwrites playback on stop`() {
    AAPlayback(file = "/movies/brave heart.mkv", position = 55, activeAudioTrack = 1, activeSubtitle = 1).save()
    val status = PlayerStatus(file = "/movies/brave heart.mkv", position = 111, activeAudioTrack = 1, activeSubtitle = 2)
    whenInvoke(service.stop()).thenReturn(Single.just(status))
    assertThat(underTest.stop().blockingGet()).isEqualTo(status)
    assertThat(Select().from(AAPlayback::class.java).where("file = ?", status.file).execute<AAPlayback>())
        .containsOnly(AAPlayback(file = "/movies/brave heart.mkv", position = 111, activeAudioTrack = 1, activeSubtitle = 2))
  }

  @Test(expected = RuntimeException::class)
  fun `does not save playback when error occurred while stop`() {
    val error = RuntimeException()
    whenInvoke(service.stop()).thenThrow(error)
    try {
      underTest.stop().blockingGet()
    } catch (e: Exception) {
      assertThat(Select().from(AAPlayback::class.java).count()).isZero()
      throw e
    }
  }

  @Test fun `toggles mute`() {
    val status = PlayerStatus(file = "/movies/brave heart.mkv", muted = true)
    whenInvoke(service.toggleMute()).thenReturn(Single.just(status))
    assertThat(underTest.toggleMute().blockingGet()).isEqualTo(status)
  }

  @Test fun `toggles subtitles`() {
    val status = PlayerStatus(file = "/movies/brave heart.mkv", subtitlesOff = true)
    whenInvoke(service.toggleSubtitles()).thenReturn(Single.just(status))
    assertThat(underTest.toggleSubtitles().blockingGet()).isEqualTo(status)
  }

  @Test fun `decreases volume`() {
    val vol = Volume(0.3f)
    whenInvoke(service.volumeDown()).thenReturn(Single.just(vol))
    assertThat(underTest.volumeDown().blockingGet()).isEqualTo(vol)
  }

  @Test fun `increases volume`() {
    val vol = Volume(0.5f)
    whenInvoke(service.volumeUp()).thenReturn(Single.just(vol))
    assertThat(underTest.volumeUp().blockingGet()).isEqualTo(vol)
  }
}
