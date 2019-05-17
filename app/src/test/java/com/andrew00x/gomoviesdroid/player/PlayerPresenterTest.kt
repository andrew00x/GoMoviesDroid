package com.andrew00x.gomoviesdroid.player

import com.andrew00x.gomoviesdroid.*
import io.reactivex.Single
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.reset
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when` as whenInvoke
import org.mockito.junit.MockitoJUnitRunner
import java.util.concurrent.TimeUnit

@RunWith(MockitoJUnitRunner::class)
class PlayerPresenterTest {

  @Rule
  @JvmField
  val schedulers = SchedulersSetup()

  @Mock lateinit var view: PlayerView
  @Mock lateinit var events: PlayerEventSource
  @Mock lateinit var errorHandler: ErrorHandler
  @Mock lateinit var model: PlayerModel
  @InjectMocks lateinit var presenter: PlayerPresenter

  private lateinit var refreshEvent: Event<Any>
  private lateinit var playPauseEvent: Event<Any>
  private lateinit var stopEvent: Event<Any>
  private lateinit var replayEvent: Event<Any>
  private lateinit var forward10minEvent: Event<Any>
  private lateinit var rewind10minEvent: Event<Any>
  private lateinit var forward30secEvent: Event<Any>
  private lateinit var rewind30secEvent: Event<Any>
  private lateinit var volumeUpEvent: Event<Any>
  private lateinit var volumeDownEvent: Event<Any>
  private lateinit var seekPositionEvent: Event<Int>
  private lateinit var nextAudioTrackEvent: Event<Any>
  private lateinit var previousAudioTrackEvent: Event<Any>
  private lateinit var nextSubtitleEvent: Event<Any>
  private lateinit var previousSubtitleEvent: Event<Any>
  private lateinit var toggleMuteEvent: Event<Any>
  private lateinit var toggleSubtitlesEvent: Event<Any>

  @Before
  fun setup() {
    whenInvoke(model.getStatus()).thenReturn(Single.just(PlayerStatus(file = "")))
    refreshEvent = Event()
    playPauseEvent = Event()
    stopEvent = Event()
    replayEvent = Event()
    forward10minEvent = Event()
    rewind10minEvent = Event()
    forward30secEvent = Event()
    rewind30secEvent = Event()
    volumeUpEvent = Event()
    volumeDownEvent = Event()
    seekPositionEvent = Event()
    nextAudioTrackEvent = Event()
    previousAudioTrackEvent = Event()
    nextSubtitleEvent = Event()
    previousSubtitleEvent = Event()
    toggleMuteEvent = Event()
    toggleSubtitlesEvent = Event()
    mockEventSource()
  }

  @After
  fun cleanup() {
    presenter.detach()
  }

  @Test
  fun `refresh on attach`() {
    val status = PlayerStatus(file = "/movies/Brave heart.mkv", position = sec(30), duration = sec(150), muted = true)
    whenInvoke(model.getStatus()).thenReturn(Single.just(status))

    presenter.attach(view, events)

    assertViewUpdated(status)
  }

  @Test
  fun `refresh on refresh event`() {
    presenter.attach(view, events)
    reset(view)
    val status = PlayerStatus(file = "/movies/Brave heart.mkv", position = sec(30), duration = sec(150), muted = true)
    whenInvoke(model.getStatus()).thenReturn(Single.just(status))

    refreshEvent.send(Unit)

    assertViewUpdated(status)
  }

  @Test
  fun refresh() {
    presenter.attach(view, events)
    reset(view)
    val status = PlayerStatus(file = "/movies/Brave heart.mkv", position = sec(30), duration = sec(150), muted = true)
    whenInvoke(model.getStatus()).thenReturn(Single.just(status))

    presenter.refresh(view)

    assertViewUpdated(status)
  }

  @Test
  fun `toggle play | pause`() {
    presenter.attach(view, events)
    reset(view)
    val status = PlayerStatus(file = "/movies/Brave heart.mkv", position = 0, duration = sec(150))
    whenInvoke(model.playPause()).thenReturn(Single.just(status))

    playPauseEvent.send(Unit)

    assertViewUpdated(status)
  }

  @Test
  fun stop() {
    presenter.attach(view, events)
    reset(view)
    val status = PlayerStatus(file = "/movies/brave heart.mkv", stopped = true)
    whenInvoke(model.stop()).thenReturn(Single.just(status))

    stopEvent.send(Unit)

    assertViewUpdated(status)
  }

  @Test
  fun replay() {
    presenter.attach(view, events)
    reset(view)
    val status = PlayerStatus(file = "/movies/brave heart.mkv")
    whenInvoke(model.replay()).thenReturn(Single.just(status))

    replayEvent.send(Unit)

    assertViewUpdated(status)
  }

  @Test
  fun `forward 10 min`() {
    presenter.attach(view, events)
    reset(view)
    val status = PlayerStatus(file = "/movies/Brave heart.mkv", position = sec(20), duration = sec(150))
    whenInvoke(model.forward(sec(10))).thenReturn(Single.just(status))

    forward10minEvent.send(Unit)

    assertViewUpdated(status)
  }

  @Test
  fun `rewind 10 min`() {
    presenter.attach(view, events)
    reset(view)
    val status = PlayerStatus(file = "/movies/Brave heart.mkv", position = sec(10), duration = sec(150))
    whenInvoke(model.rewind(sec(10))).thenReturn(Single.just(status))

    rewind10minEvent.send(Unit)

    assertViewUpdated(status)
  }

  @Test
  fun `forward 30 sec`() {
    presenter.attach(view, events)
    reset(view)
    val status = PlayerStatus(file = "/movies/Brave heart.mkv", position = 60, duration = sec(150))
    whenInvoke(model.forward(30)).thenReturn(Single.just(status))

    forward30secEvent.send(Unit)

    assertViewUpdated(status)
  }

  @Test
  fun `rewind 30 sec`() {
    presenter.attach(view, events)
    reset(view)
    val status = PlayerStatus(file = "/movies/Brave heart.mkv", position = 30, duration = sec(150))
    whenInvoke(model.rewind(30)).thenReturn(Single.just(status))

    rewind30secEvent.send(Unit)

    assertViewUpdated(status)
  }

  @Test
  fun `seek position`() {
    presenter.attach(view, events)
    reset(view)
    val pos = sec(33)
    val status = PlayerStatus(file = "/movies/Brave heart.mkv", position = pos, duration = sec(150))
    whenInvoke(model.setPosition(pos)).thenReturn(Single.just(status))

    seekPositionEvent.send(pos)
    schedulers.computationScheduler.advanceTimeBy(300, TimeUnit.MILLISECONDS)
    schedulers.computationScheduler.triggerActions()

    assertViewUpdated(status)
  }

  @Test
  fun `volume up`() {
    presenter.attach(view, events)
    reset(view)
    val vol = Volume(0.8F)
    whenInvoke(model.volumeUp()).thenReturn(Single.just(vol))

    volumeUpEvent.send(Unit)

    verify(view).showLoader()
    verify(view).hideLoader()
    verify(view).showVolumeLevel(vol)
  }

  @Test
  fun `volume down`() {
    presenter.attach(view, events)
    reset(view)
    val vol = Volume(0.5F)
    whenInvoke(model.volumeDown()).thenReturn(Single.just(vol))

    volumeDownEvent.send(Unit)

    verify(view).showLoader()
    verify(view).hideLoader()
    verify(view).showVolumeLevel(vol)
  }

  @Test
  fun `next audio track`() {
    presenter.attach(view, events)
    reset(view)
    val audios = listOf(Stream(index = 1, lang = "ukr"), Stream(index = 2, lang = "en", active = true))
    whenInvoke(model.switchToNextAudioTrack()).thenReturn(Single.just(audios))

    nextAudioTrackEvent.send(Unit)

    verify(view).showLoader()
    verify(view).hideLoader()
    verify(view).showAudioStreams(audios)
  }

  @Test
  fun `previous audio track`() {
    presenter.attach(view, events)
    reset(view)
    val audios = listOf(Stream(index = 1, lang = "ukr", active = true), Stream(index = 2, lang = "en"))
    whenInvoke(model.switchToPreviousAudioTrack()).thenReturn(Single.just(audios))

    previousAudioTrackEvent.send(Unit)

    verify(view).showLoader()
    verify(view).hideLoader()
    verify(view).showAudioStreams(audios)
  }

  @Test
  fun `next subtitle`() {
    presenter.attach(view, events)
    reset(view)
    val subs = listOf(Stream(index = 1, lang = "ukr"), Stream(index = 2, lang = "en", active = true))
    whenInvoke(model.switchToNextSubtitle()).thenReturn(Single.just(subs))

    nextSubtitleEvent.send(Unit)

    verify(view).showLoader()
    verify(view).hideLoader()
    verify(view).showSubtitles(subs)
  }

  @Test
  fun `previous subtitle`() {
    presenter.attach(view, events)
    reset(view)
    val subs = listOf(Stream(index = 1, lang = "ukr", active = true), Stream(index = 2, lang = "en"))
    whenInvoke(model.switchToPreviousSubtitle()).thenReturn(Single.just(subs))

    previousSubtitleEvent.send(Unit)

    verify(view).showLoader()
    verify(view).hideLoader()
    verify(view).showSubtitles(subs)
  }

  @Test
  fun `toggle mute`() {
    presenter.attach(view, events)
    reset(view)
    val status = PlayerStatus(file = "/movies/Brave heart.mkv", muted = true)
    whenInvoke(model.toggleMute()).thenReturn(Single.just(status))

    toggleMuteEvent.send(Unit)

    verify(view).showLoader()
    verify(view).hideLoader()
    verify(view).showMuted(true)
  }

  @Test
  fun `toggle subtitles`() {
    presenter.attach(view, events)
    reset(view)
    val status = PlayerStatus(file = "/movies/Brave heart.mkv", subtitlesOff = true)
    whenInvoke(model.toggleSubtitles()).thenReturn(Single.just(status))

    toggleSubtitlesEvent.send(Unit)

    verify(view).showLoader()
    verify(view).hideLoader()
    verify(view).showSubtitlesOff(true)
  }

  @Test
  fun `handle error occurred while refresh`() {
    presenter.attach(view, events)
    reset(view)
    val err = RuntimeException("failed")
    whenInvoke(model.getStatus()).thenReturn(Single.error(err))

    presenter.refresh(view)

    verify(view).showLoader()
    verify(view).hideLoader()
    verify(errorHandler).handleError(view, err)
  }

  @Test
  fun `handle error occurred on refresh event`() {
    presenter.attach(view, events)
    reset(view)
    val err = RuntimeException("failed")
    whenInvoke(model.getStatus()).thenReturn(Single.error(err))

    refreshEvent.send(Unit)

    verify(view).showLoader()
    verify(view).hideLoader()
    verify(errorHandler).handleError(view, err)
  }

  @Test
  fun `handle error occurred while toggle play | pause`() {
    presenter.attach(view, events)
    reset(view)
    val err = RuntimeException("failed")
    whenInvoke(model.playPause()).thenReturn(Single.error(err))

    playPauseEvent.send(Unit)

    verify(view).showLoader()
    verify(view).hideLoader()
    verify(errorHandler).handleError(view, err)
  }

  @Test
  fun `handle error occurred while stop`() {
    presenter.attach(view, events)
    reset(view)
    val err = RuntimeException("failed")
    whenInvoke(model.stop()).thenReturn(Single.error(err))

    stopEvent.send(Unit)

    verify(view).showLoader()
    verify(view).hideLoader()
    verify(errorHandler).handleError(view, err)
  }

  @Test
  fun `handle error occurred while move forward 10 min`() {
    presenter.attach(view, events)
    reset(view)
    val err = RuntimeException("failed")
    whenInvoke(model.forward(sec(10))).thenReturn(Single.error(err))

    forward10minEvent.send(Unit)

    verify(view).showLoader()
    verify(view).hideLoader()
    verify(errorHandler).handleError(view, err)
  }

  @Test
  fun `handle error occurred while move forward 30 sec`() {
    presenter.attach(view, events)
    reset(view)
    val err = RuntimeException("failed")
    whenInvoke(model.forward(30)).thenReturn(Single.error(err))

    forward30secEvent.send(Unit)

    verify(view).showLoader()
    verify(view).hideLoader()
    verify(errorHandler).handleError(view, err)
  }

  @Test
  fun `handle error occurred while rewind 10 min`() {
    presenter.attach(view, events)
    reset(view)
    val err = RuntimeException("failed")
    whenInvoke(model.rewind(sec(10))).thenReturn(Single.error(err))

    rewind10minEvent.send(Unit)

    verify(view).showLoader()
    verify(view).hideLoader()
    verify(errorHandler).handleError(view, err)
  }

  @Test
  fun `handle error occurred while rewind 30 sec`() {
    presenter.attach(view, events)
    reset(view)
    val err = RuntimeException("failed")
    whenInvoke(model.rewind(30)).thenReturn(Single.error(err))

    rewind30secEvent.send(Unit)

    verify(view).showLoader()
    verify(view).hideLoader()
    verify(errorHandler).handleError(view, err)
  }

  @Test
  fun `handle error occurred while change position`() {
    presenter.attach(view, events)
    reset(view)
    val pos = sec(33)
    val err = RuntimeException("failed")
    whenInvoke(model.setPosition(pos)).thenReturn(Single.error(err))

    seekPositionEvent.send(pos)
    schedulers.computationScheduler.advanceTimeBy(300, TimeUnit.MILLISECONDS)
    schedulers.computationScheduler.triggerActions()

    verify(view).showLoader()
    verify(view).hideLoader()
    verify(errorHandler).handleError(view, err)
  }

  @Test
  fun `handle error occurred while volume up`() {
    presenter.attach(view, events)
    reset(view)
    val pos = sec(33)
    val err = RuntimeException("failed")
    whenInvoke(model.volumeUp()).thenReturn(Single.error(err))

    volumeUpEvent.send(pos)

    verify(view).showLoader()
    verify(view).hideLoader()
    verify(errorHandler).handleError(view, err)
  }

  @Test
  fun `handle error occurred while volume down`() {
    presenter.attach(view, events)
    reset(view)
    val pos = sec(33)
    val err = RuntimeException("failed")
    whenInvoke(model.volumeDown()).thenReturn(Single.error(err))

    volumeDownEvent.send(pos)

    verify(view).showLoader()
    verify(view).hideLoader()
    verify(errorHandler).handleError(view, err)
  }

  @Test
  fun `handle error occurred while switch to next audio track`() {
    presenter.attach(view, events)
    reset(view)
    val err = RuntimeException("failed")
    whenInvoke(model.switchToNextAudioTrack()).thenReturn(Single.error(err))

    nextAudioTrackEvent.send(Unit)

    verify(view).showLoader()
    verify(view).hideLoader()
    verify(errorHandler).handleError(view, err)
  }

  @Test
  fun `handle error occurred while switch to previous audio track`() {
    presenter.attach(view, events)
    reset(view)
    val err = RuntimeException("failed")
    whenInvoke(model.switchToPreviousAudioTrack()).thenReturn(Single.error(err))

    previousAudioTrackEvent.send(Unit)

    verify(view).showLoader()
    verify(view).hideLoader()
    verify(errorHandler).handleError(view, err)
  }
  @Test
  fun `handle error occurred while switch to next subtitle`() {
    presenter.attach(view, events)
    reset(view)
    val err = RuntimeException("failed")
    whenInvoke(model.switchToNextSubtitle()).thenReturn(Single.error(err))

    nextSubtitleEvent.send(Unit)

    verify(view).showLoader()
    verify(view).hideLoader()
    verify(errorHandler).handleError(view, err)
  }

  @Test
  fun `handle error occurred while switch to previous subtitle`() {
    presenter.attach(view, events)
    reset(view)
    val err = RuntimeException("failed")
    whenInvoke(model.switchToPreviousSubtitle()).thenReturn(Single.error(err))

    previousSubtitleEvent.send(Unit)

    verify(view).showLoader()
    verify(view).hideLoader()
    verify(errorHandler).handleError(view, err)
  }

  @Test
  fun `handle error occurred while toggle mute`() {
    presenter.attach(view, events)
    reset(view)
    val err = RuntimeException("failed")
    whenInvoke(model.toggleMute()).thenReturn(Single.error(err))

    toggleMuteEvent.send(Unit)

    verify(view).showLoader()
    verify(view).hideLoader()
    verify(errorHandler).handleError(view, err)
  }

  @Test
  fun `handle error occurred while toggle subtitles`() {
    presenter.attach(view, events)
    reset(view)
    val err = RuntimeException("failed")
    whenInvoke(model.toggleSubtitles()).thenReturn(Single.error(err))

    toggleSubtitlesEvent.send(Unit)

    verify(view).showLoader()
    verify(view).hideLoader()
    verify(errorHandler).handleError(view, err)
  }

  private fun mockEventSource() {
    whenInvoke(events.clickRefresh()).thenReturn(refreshEvent)
    whenInvoke(events.clickPlayPause()).thenReturn(playPauseEvent)
    whenInvoke(events.clickStop()).thenReturn(stopEvent)
    whenInvoke(events.clickReplay()).thenReturn(replayEvent)
    whenInvoke(events.clickForward10min()).thenReturn(forward10minEvent)
    whenInvoke(events.clickRewind10min()).thenReturn(rewind10minEvent)
    whenInvoke(events.clickForward30sec()).thenReturn(forward30secEvent)
    whenInvoke(events.clickRewind30sec()).thenReturn(rewind30secEvent)
    whenInvoke(events.clickVolumeUp()).thenReturn(volumeUpEvent)
    whenInvoke(events.clickVolumeDown()).thenReturn(volumeDownEvent)
    whenInvoke(events.clickNextAudioTrack()).thenReturn(nextAudioTrackEvent)
    whenInvoke(events.clickPreviousAudioTrack()).thenReturn(previousAudioTrackEvent)
    whenInvoke(events.clickNextSubtitles()).thenReturn(nextSubtitleEvent)
    whenInvoke(events.clickPreviousSubtitles()).thenReturn(previousSubtitleEvent)
    whenInvoke(events.clickToggleMute()).thenReturn(toggleMuteEvent)
    whenInvoke(events.clickToggleSubtitles()).thenReturn(toggleSubtitlesEvent)
    whenInvoke(events.seekPosition()).thenReturn(seekPositionEvent)
  }

  private fun assertViewUpdated(status: PlayerStatus) {
    verify(view).showLoader()
    verify(view).hideLoader()
    verify(view).showPlayingNow(status.file.substringAfterLast('/'))
    verify(view).showProgress(status.position, status.duration)
    verify(view).showMuted(status.muted)
    verify(view).showPaused(status.paused)
    verify(view).showSubtitlesOff(status.subtitlesOff)
  }

  private fun sec(minutes: Int): Int {
    return TimeUnit.MINUTES.toSeconds(minutes.toLong()).toInt()
  }
}