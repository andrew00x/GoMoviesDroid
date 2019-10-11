package com.andrew00x.gomoviesdroid.ui

import android.support.v4.app.Fragment

abstract class BaseFragment : Fragment(), BaseView {
  override fun showInfo(message: String) {
    if (activity is BaseView) (activity as BaseView).showInfo(message)
  }

  override fun showError(message: String?) {
    if (activity is BaseView) (activity as BaseView).showError(message)
  }

  override fun showLoader() {
    if (activity is BaseView) (activity as BaseView).showLoader()
  }

  override fun hideLoader() {
    if (activity is BaseView) (activity as BaseView).hideLoader()
  }

  open fun refresh() {}
}