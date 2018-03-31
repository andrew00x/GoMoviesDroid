package com.andrew00x.gomoviesdroid.main

import com.andrew00x.gomoviesdroid.BasePresenter
import io.reactivex.disposables.CompositeDisposable

class MainPresenter (private val model: MainModel, private val view: MainView) : BasePresenter {
    private val subscriptions: CompositeDisposable = CompositeDisposable()

    override fun start() {
        subscriptions.add(model.changePage().subscribe { view.refresh() })
    }

    override fun stop() {
        subscriptions.dispose()
    }

    override fun refresh() {
    }
}