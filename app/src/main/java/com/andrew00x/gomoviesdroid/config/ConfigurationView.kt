package com.andrew00x.gomoviesdroid.config

import com.andrew00x.gomoviesdroid.BaseView

interface ConfigurationView : BaseView {
    fun showConfiguration(configuration: Configuration)
    fun onConfigurationSaved()
}