package com.andrew00x.gomoviesdroid.config

import com.andrew00x.gomoviesdroid.ErrorHandler
import com.andrew00x.gomoviesdroid.Event
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.InjectMocks
import org.mockito.Mockito
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when` as whenInvoke
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ConfigurationPresenterTest {
  @Mock private lateinit var model: ConfigurationModel
  @Mock private lateinit var listener: ConfigurationListener
  @Mock private lateinit var errorHandler: ErrorHandler
  @Mock private lateinit var view: ConfigurationView
  @InjectMocks private lateinit var underTest: ConfigurationPresenter
  private lateinit var clickOnSave: Event<Any>

  @Before fun setup() {
    clickOnSave = Event()
    whenInvoke(view.clickOnSave()).thenReturn(clickOnSave)
  }

  @Test fun `sets view with values when attach`() {
    val config = Configuration("gomovies.local", 8000, setOf("en"))
    whenInvoke(model.get()).thenReturn(config)
    underTest.attach(view)
    verify(view).setServer("gomovies.local")
    verify(view).setPort(8000)
    verify(view).setDetailLanguages(setOf("en"))
  }

  @Test fun `notifies about error occurred while read configuration`() {
    val error = RuntimeException()
    whenInvoke(model.get()).thenThrow(error)
    underTest.attach(view)
    verify(errorHandler).handleError(view, error)
  }

  @Test fun `saves config when saved button is clicked`() {
    whenInvoke(model.get()).thenReturn(Configuration("gomovies.local", 8000, setOf("en")))
    whenInvoke(view.getServer()).thenReturn("gomovies.local2")
    whenInvoke(view.getPort()).thenReturn(8001)
    whenInvoke(view.getDetailLanguages()).thenReturn(setOf("en", "ua"))
    underTest.attach(view)

    clickOnSave.send(Unit)

    val updated = Configuration("gomovies.local2", 8001, setOf("en", "ua"))
    val inOrder = Mockito.inOrder(model, listener)
    inOrder.verify(model).save(updated)
    inOrder.verify(listener).afterSave(updated)
  }

  @Test fun `notifies about error occurred while save configuration`() {
    val config = Configuration("gomovies.local", 8000, setOf("en"))
    val error = RuntimeException()
    whenInvoke(model.get()).thenReturn(config)
    whenInvoke(view.getServer()).thenReturn(config.server)
    whenInvoke(view.getPort()).thenReturn(config.port)
    whenInvoke(view.getDetailLanguages()).thenReturn(config.detailLangs)
    whenInvoke(model.save(config)).thenThrow(error)
    underTest.attach(view)

    clickOnSave.send(Unit)

    verify(errorHandler).handleError(view, error)
    verify(listener, never()).afterSave(config)
  }
}
