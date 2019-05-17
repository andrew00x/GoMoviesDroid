package com.andrew00x.gomoviesdroid

interface BaseView {
  fun refresh()
  fun showLoader()
  fun hideLoader()
  fun showError(message: String?)
}