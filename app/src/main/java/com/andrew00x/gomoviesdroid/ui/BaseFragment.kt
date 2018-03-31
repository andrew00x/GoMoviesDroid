package com.andrew00x.gomoviesdroid.ui

import android.support.v4.app.Fragment
import com.andrew00x.gomoviesdroid.BasePresenter
import com.andrew00x.gomoviesdroid.BaseView

abstract class BaseFragment : Fragment(), BaseView {
    abstract protected fun getPresenter(): BasePresenter?

    override fun refresh() {
        getPresenter()?.refresh()
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

    override fun onDestroyView() {
        super.onDestroyView()
        getPresenter()?.stop()
    }
}