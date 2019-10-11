package com.andrew00x.gomoviesdroid.config

interface ConfigurationListener {
  fun afterSave(config: Configuration)
}
