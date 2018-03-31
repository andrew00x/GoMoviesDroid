package com.andrew00x.gomoviesdroid.config

import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable

class ConfigurationModel(private val configurationService: ConfigurationService,
                         private val serverUrl: EditText,
                         private val savePlaybackOnStop: CheckBox,
                         private val saveConfiguration: View) {

    fun changeServerUrl(): Observable<String> {
        return RxTextView
                .textChanges(serverUrl)
                .map { value -> value.toString() }
    }

    fun toggleSavePlaybackOnStop(): Observable<Boolean> {
        return Observable.create<Boolean> { emitter ->
            emitter.setCancellable { savePlaybackOnStop.setOnCheckedChangeListener(null) }
            savePlaybackOnStop.setOnCheckedChangeListener { _, isChecked -> emitter.onNext(isChecked) }
        }
    }

    fun clickOnSaveConfiguration(): Observable<Any> {
        return RxView.clicks(saveConfiguration)
    }

    fun readConfiguration(): Observable<Configuration> {
        return Observable.fromCallable { configurationService.retrieve() }
    }

    fun saveConfiguration(config: Configuration): Observable<Any> {
        return Observable.fromCallable { configurationService.save(validateAndAdjust(config)) }
    }

    private fun validateAndAdjust(config: Configuration): Configuration {
        if (!config.serverUrl.endsWith("/")) {
            config.serverUrl += "/"
        }
        return config
    }
}

