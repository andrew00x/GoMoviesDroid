package com.andrew00x.gomoviesdroid.config

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.Test

class ConfigurationTest {
  @Test fun `builds url`() {
    val config = Configuration("gomovies.local", 8000, setOf())
    assertThat(config.baseUrl).isEqualTo("http://gomovies.local:8000/api/")
  }
}
