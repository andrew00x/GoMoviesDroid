package com.andrew00x.gomoviesdroid.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.andrew00x.gomoviesdroid.GomoviesApplication
import com.andrew00x.gomoviesdroid.R
import com.andrew00x.gomoviesdroid.config.*
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import javax.inject.Inject

class ConfigurationFragment : ConfigurationView, ConfigurationEventSource, BaseFragment() {
  private lateinit var server: EditText
  private lateinit var port: EditText
  private lateinit var saveConfiguration: View

  @Inject lateinit var presenter: ConfigurationPresenter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    (activity.application as GomoviesApplication).component.injectInto(this)
  }

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    val view = inflater?.inflate(R.layout.fragment_configuration, container, false)!!
    initView(view)
    presenter.attach(this, this)
    return view
  }

  private fun initView(view: View) {
    server = view.findViewById(R.id.server_edit)
    port = view.findViewById(R.id.port_edit)
    saveConfiguration = view.findViewById(R.id.save_configuration_button)
  }

  override fun onDestroy() {
    super.onDestroy()
    presenter.detach()
  }

  override fun refresh() {
    presenter.refresh(this)
  }

  override fun onConfigurationSaved() {
    activity.recreate()
  }

  override fun showServer(server: String) {
    this.server.setText(server)
  }

  override fun showPort(port: Int) {
    this.port.setText(port.toString())
  }

  override fun changeServer(): Observable<String> {
    return RxTextView
        .textChanges(server)
        .map { value -> value.toString() }
  }

  override fun changePort(): Observable<String> {
    return RxTextView
        .textChanges(port)
        .map { value -> value.toString() }
  }

  override fun clickOnSaveConfiguration(): Observable<Any> {
    return RxView.clicks(saveConfiguration)
  }
}