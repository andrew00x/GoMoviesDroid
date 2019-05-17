package com.andrew00x.gomoviesdroid.config

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.andrew00x.gomoviesdroid.ErrorHandler
import com.andrew00x.gomoviesdroid.Event
import com.andrew00x.gomoviesdroid.SchedulersSetup
import io.reactivex.Single
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.InjectMocks
import org.mockito.Mockito.`when` as whenInvoke
import org.mockito.Mockito.verify
import org.mockito.Mockito.reset
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ConfigurationPresenterTest {
  @JvmField
  @Rule
  val schedulers = SchedulersSetup()

  @Mock lateinit var model: ConfigurationModel
  @Mock lateinit var errorHandler: ErrorHandler
  @Mock lateinit var view: ConfigurationView
  @Mock lateinit var events: ConfigurationEventSource
  @InjectMocks lateinit var presenter: ConfigurationPresenter

  private lateinit var changeServer: Event<String>
  private lateinit var changePort: Event<String>
  private lateinit var clickOnSaveConfiguration: Event<Any>
  private lateinit var configuration: Configuration

  @Before
  fun setup() {
    changeServer = Event()
    changePort = Event()
    clickOnSaveConfiguration = Event()
    configuration = Configuration("", 80)

    whenInvoke(model.read()).thenReturn(Single.just(configuration))
    whenInvoke(events.changeServer()).thenReturn(changeServer)
    whenInvoke(events.changePort()).thenReturn(changePort)
    whenInvoke(events.clickOnSaveConfiguration()).thenReturn(clickOnSaveConfiguration)
  }

  @After
  fun cleanup() {
    presenter.detach()
  }

  @Test
  fun `change server`() {
    presenter.attach(view, events)
    reset(view)

    changeServer.send("my-movies")

    assertThat(configuration.server).isEqualTo("my-movies")
  }

  @Test
  fun `change port`() {
    presenter.attach(view, events)
    reset(view)

    changePort.send("1234")

    assertThat(configuration.port).isEqualTo(1234)
  }

  @Test
  fun `save configuration`() {
    presenter.attach(view, events)
    reset(view)
    whenInvoke(model.save(configuration)).thenReturn(Single.just(Unit))

    clickOnSaveConfiguration.send(Unit)

    verify(model).save(configuration)
  }

  @Test
  fun `notify ErrorHandler when error occurred while read configuration`() {
    val err = RuntimeException("failed")
    whenInvoke(model.read()).thenReturn(Single.error(err))

    presenter.attach(view, events)

    verify(errorHandler).handleError(view, err)
  }

  @Test
  fun `notify ErrorHandler when error occurred while save configuration`() {
    presenter.attach(view, events)
    reset(view)
    val err = RuntimeException("failed")
    whenInvoke(model.save(configuration)).thenReturn(Single.error(err))

    clickOnSaveConfiguration.send(Unit)

    verify(errorHandler).handleError(view, err)
  }
}