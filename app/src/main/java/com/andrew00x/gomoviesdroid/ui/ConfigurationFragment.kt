package com.andrew00x.gomoviesdroid.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import com.andrew00x.gomoviesdroid.BasePresenter
import com.andrew00x.gomoviesdroid.GomoviesApplication
import com.andrew00x.gomoviesdroid.R
import com.andrew00x.gomoviesdroid.config.*
import javax.inject.Inject

class ConfigurationFragment : ConfigurationView, BaseFragment() {
    @Inject lateinit var configurationService: ConfigurationService

    private lateinit var serverUrl: EditText
    private lateinit var savePlaybackOnStop: CheckBox
    private lateinit var saveConfiguration: View

    private var presenter: ConfigurationPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity.application as GomoviesApplication).component.injectInto(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_configuration, container, false)!!

        serverUrl = view.findViewById(R.id.server_url_edit)
        savePlaybackOnStop = view.findViewById(R.id.save_playback_on_stop)
        saveConfiguration = view.findViewById(R.id.save_configuration_button)

        val model = ConfigurationModel(configurationService, serverUrl, savePlaybackOnStop, saveConfiguration)
        val presenter = ConfigurationPresenter(model, this)
        presenter.start()
        this.presenter = presenter

        return view
    }

    override fun getPresenter(): BasePresenter? {
        return presenter
    }

    override fun onConfigurationSaved() {
        activity.recreate()
    }

    override fun showConfiguration(configuration: Configuration) {
        serverUrl.setText(configuration.serverUrl)
        savePlaybackOnStop.isChecked = configuration.savePlaybackOnStop
    }
}