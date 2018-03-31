package com.andrew00x.gomoviesdroid.config

import com.activeandroid.Model
import com.activeandroid.annotation.Column
import com.activeandroid.annotation.Table
import com.activeandroid.query.Select
import java.util.concurrent.CopyOnWriteArrayList

interface ConfigurationService {
    fun retrieve(): Configuration
    fun save(configuration: Configuration)
    fun addListener(listener: ConfigurationUpdateListener)
    fun removeListener(listener: ConfigurationUpdateListener)
}

interface ConfigurationUpdateListener {
    fun onConfigurationUpdate(updated: Configuration)
}

@Table(name = "config")
data class Configuration(
        @Column(name = "serverurl") var serverUrl: String = "",
        @Column(name = "saveplaybackonstop") var savePlaybackOnStop: Boolean = false
) : Model()

class AAConfigurationService : ConfigurationService {
    private val listeners = CopyOnWriteArrayList<ConfigurationUpdateListener>()

    override fun retrieve(): Configuration {
        return Select().from(Configuration::class.java).executeSingle() ?: Configuration()
    }

    override fun save(configuration: Configuration) {
        configuration.save()
        for (listener in listeners) {
            listener.onConfigurationUpdate(configuration)
        }
    }

    override fun addListener(listener: ConfigurationUpdateListener) {
        listeners.add(listener)
    }

    override fun removeListener(listener: ConfigurationUpdateListener) {
        listeners.remove(listener)
    }
}
