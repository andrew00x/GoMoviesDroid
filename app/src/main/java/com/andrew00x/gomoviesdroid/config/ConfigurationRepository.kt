package com.andrew00x.gomoviesdroid.config

import android.content.Context

data class Configuration(var server: String, var port: Int) {
  val baseUrl: String
    get() = "http://$server:$port/api/"
}

interface ConfigurationRepository {
  fun retrieve(): Configuration
  fun save(configuration: Configuration)
}

class SharedPreferencesConfigurationRepository(private val ctx: Context) : ConfigurationRepository {
  private val file = "preferences"
  private val serverProp = "server"
  private val portProp = "port"

  override fun retrieve(): Configuration {
    val pref = ctx.getSharedPreferences(file, Context.MODE_PRIVATE)
    val server = pref.getString(serverProp, "localhost")
    val port = pref.getInt(portProp, 8000)
    return Configuration(server, port)
  }

  override fun save(configuration: Configuration) {
    ctx.getSharedPreferences(file, Context.MODE_PRIVATE).edit()
        .putString(serverProp, configuration.server)
        .putInt(portProp, configuration.port)
        .apply()
  }
}
