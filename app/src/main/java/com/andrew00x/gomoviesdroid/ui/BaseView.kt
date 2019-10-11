package com.andrew00x.gomoviesdroid.ui

interface BaseView {
  fun showLoader()
  fun hideLoader()
  fun showError(message: String?)
  fun showInfo(message: String)
}
