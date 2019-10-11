package com.andrew00x.gomoviesdroid.config

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.andrew00x.gomoviesdroid.TestApplication
import com.andrew00x.gomoviesdroid.UrlValidator
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.lang.IllegalArgumentException
import org.mockito.Mockito.`when` as whenInvoke

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26], application = TestApplication::class, manifest = Config.NONE)
class ConfigurationModelTest {
  private lateinit var context: Context
  private lateinit var urlValidator: UrlValidator
  private lateinit var underTest: ConfigurationModel

  @Before
  fun setup() {
    urlValidator = mock(UrlValidator::class.java)
    whenInvoke(urlValidator.validate(anyString())).thenReturn(true)
    context = ApplicationProvider.getApplicationContext()
    underTest = ConfigurationModel(context, urlValidator)
  }

  @Test
  fun `gets default configuration if it does not exist`() {
    assertThat(underTest.get()).isEqualTo(Configuration("10.0.2.2", 8000, setOf("en")))
  }

  @Test
  fun `gets configuration`() {
    val config = Configuration("gomovies.local", 8000, setOf("ua"))
    save(config)
    assertThat(underTest.get()).isEqualTo(config)
  }

  @Test
  fun `saves configuration`() {
    val config = Configuration("gomovies.local", 8000, setOf("ua"))
    underTest.save(config)
    assertThat(get()).isEqualTo(config)
  }

  @Test(expected = IllegalArgumentException::class)
  fun `does not save invalid configuration`() {
    val config = Configuration("gomovies.local", 8000, setOf("en"))
    whenInvoke(urlValidator.validate(config.baseUrl)).thenReturn(false)
    try {
      underTest.save(config)
    } catch (e: IllegalArgumentException) {
      assertThat(get()).isEqualTo(Configuration("", 0, setOf()))
      throw e
    }
  }

  private fun get(): Configuration {
    val pref = context.getSharedPreferences("gomovies_settings", Context.MODE_PRIVATE)
    return Configuration(pref.getString("server", "")!!, pref.getInt("port", 0), pref.getStringSet("detail_langs", setOf())!!)
  }

  private fun save(config: Configuration) {
    context.getSharedPreferences("gomovies_settings", Context.MODE_PRIVATE)
        .edit()
        .putString("server", config.server)
        .putInt("port", config.port)
        .putStringSet("detail_langs", config.detailLangs)
        .apply()
  }
}
