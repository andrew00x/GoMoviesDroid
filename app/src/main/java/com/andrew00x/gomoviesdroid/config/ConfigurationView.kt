package com.andrew00x.gomoviesdroid.config

import com.andrew00x.gomoviesdroid.BaseView

interface ConfigurationView : BaseView {
  fun showServer(server: String)
  fun showPort(port: Int)
  fun onConfigurationSaved()
}