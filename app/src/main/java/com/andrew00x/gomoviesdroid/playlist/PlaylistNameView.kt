package com.andrew00x.gomoviesdroid.playlist

import com.andrew00x.gomoviesdroid.ui.BaseView
import io.reactivex.Observable

interface PlaylistNameView : BaseView {
  fun setName(name: String)
  fun getName(): String
  fun clickOnSave(): Observable<Any>
}
