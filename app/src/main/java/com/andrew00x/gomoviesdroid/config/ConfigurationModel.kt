package com.andrew00x.gomoviesdroid.config

import android.content.Context
import com.andrew00x.gomoviesdroid.UrlValidator
import javax.inject.Inject

data class Configuration(val server: String, val port: Int, val detailLangs: Set<String>) {
  val baseUrl: String
    get() = "http://$server:$port/api/"
}

class ConfigurationModel @Inject constructor(
    private val context: Context,
    private val urlValidator: UrlValidator
) {
  companion object {
    private const val FILE = "gomovies_settings"
    private const val SERVER_PROP = "server"
    private const val PORT_PROP = "port"
    private const val DETAIL_LANGS = "detail_langs"
  }

  fun get(): Configuration {
    val pref = context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
    val server = pref.getString(SERVER_PROP, "10.0.2.2")!!
    val port = pref.getInt(PORT_PROP, 8000)
    val detailLangs = pref.getStringSet(DETAIL_LANGS, setOf("en"))!!
    return Configuration(server, port, detailLangs)
  }

  fun save(config: Configuration) {
    validate(config)
    context.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit()
        .putString(SERVER_PROP, config.server)
        .putInt(PORT_PROP, config.port)
        .putStringSet(DETAIL_LANGS, config.detailLangs)
        .apply()
  }

  private fun validate(config: Configuration) {
    require(urlValidator.validate(config.baseUrl)) { "Invalid url: ${config.baseUrl}" }
  }
}
