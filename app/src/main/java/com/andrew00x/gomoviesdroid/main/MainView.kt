package com.andrew00x.gomoviesdroid.main

import com.andrew00x.gomoviesdroid.BaseView
import com.andrew00x.gomoviesdroid.PlaybackListener

interface MainView : PlaybackListener, BaseView {
    fun showPage(page: ViewPage)
    fun removePage(page: ViewPage)
    fun hideKeyboard()
}