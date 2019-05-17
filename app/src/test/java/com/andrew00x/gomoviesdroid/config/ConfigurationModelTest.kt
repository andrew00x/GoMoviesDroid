package com.andrew00x.gomoviesdroid.config

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.andrew00x.gomoviesdroid.SchedulersSetup
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.Mockito.`when` as whenInvoke

@RunWith(MockitoJUnitRunner::class)
class ConfigurationModelTest {
  @Rule
  @JvmField
  val schedulers = SchedulersSetup()

  @Mock lateinit var repository: ConfigurationRepository
  @InjectMocks lateinit var model: ConfigurationModel

  @Test
  fun `read configuration`() {
    val config = Configuration(server = "my-movies", port = 1234)
    whenInvoke(repository.retrieve()).thenReturn(config)
    assertThat(model.read().blockingGet()).isEqualTo(config)
  }

  @Test
  fun `save configuration`() {
    val config = Configuration(server = "my-movies", port = 1234)
    model.save(config).blockingGet()
    verify(repository).save(config)
  }
}