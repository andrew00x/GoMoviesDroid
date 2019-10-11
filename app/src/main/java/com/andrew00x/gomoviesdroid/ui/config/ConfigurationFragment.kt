package com.andrew00x.gomoviesdroid.ui.config

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.andrew00x.gomoviesdroid.ConfigurationModule
import com.andrew00x.gomoviesdroid.GomoviesApplication
import com.andrew00x.gomoviesdroid.R
import com.andrew00x.gomoviesdroid.config.*
import com.andrew00x.gomoviesdroid.ui.BaseFragment
import com.andrew00x.gomoviesdroid.ui.MainActivity
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import javax.inject.Inject

class ConfigurationFragment : ConfigurationView, BaseFragment() {
  companion object {
    fun newInstance(): ConfigurationFragment = ConfigurationFragment()
  }

  private lateinit var components: ConfigurationViewHolder

  @Inject
  lateinit var presenter: ConfigurationPresenter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    (activity!!.application as GomoviesApplication).component.plus(ConfigurationModule(activity as MainActivity)).inject(this)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.fragment_configuration, container, false)!!
    components = ConfigurationViewHolder(view)
    return view
  }

  override fun onResume() {
    super.onResume()
    presenter.attach(this)
  }

  override fun onPause() {
    presenter.detach()
    super.onPause()
  }

  override fun getServer() = components.server.text.toString()

  override fun setServer(server: String) = components.server.setText(server)

  override fun getPort(): Int {
    val s = components.port.text.toString()
    return if (s.isBlank()) -1 else s.toInt()
  }

  override fun setPort(port: Int) = components.port.setText(port.toString())

  override fun getDetailLanguages(): Set<String> {
    val s = components.detailLangs.text.toString()
    return if (s.isBlank()) setOf() else s.split(",").map { it.trim() }.toSet()
  }

  override fun setDetailLanguages(langs: Set<String>) = components.detailLangs.setText(langs.joinToString(", "))


  override fun clickOnSave(): Observable<Any> {
    return RxView.clicks(components.save)
  }
}
