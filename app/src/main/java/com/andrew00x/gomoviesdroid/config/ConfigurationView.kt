package com.andrew00x.gomoviesdroid.config

import com.andrew00x.gomoviesdroid.ui.BaseView
import io.reactivex.Observable

interface ConfigurationView : BaseView {
  fun getServer(): String
  fun setServer(server: String)
  fun setPort(port: Int)
  fun getPort(): Int
  fun getDetailLanguages(): Set<String>
  fun setDetailLanguages(langs: Set<String>)
  fun clickOnSave(): Observable<Any>
}
