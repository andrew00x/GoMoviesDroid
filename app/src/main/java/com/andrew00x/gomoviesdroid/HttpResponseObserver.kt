package com.andrew00x.gomoviesdroid

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.observers.DisposableObserver
import retrofit2.HttpException

abstract class HttpResponseObserver<T>(private val view: BaseView) : DisposableObserver<T>() {
    companion object {
        @JvmField
        val gson: Gson = GsonBuilder().create()
    }

    private data class ErrorMessage(var message: String = "")

    abstract fun onSuccess(body: T)

    open fun onError(message: String?) {
        view.showError(message)
    }

    override fun onNext(body: T) {
        onSuccess(body)
    }

    override fun onStart() {
        mainThread().scheduleDirect { view.showLoader() }
    }

    override fun onComplete() {
        mainThread().scheduleDirect { view.hideLoader() }
    }

    override fun onError(err: Throwable) {
        mainThread().scheduleDirect { view.hideLoader() }
        if (err is HttpException) {
            onError(gson.fromJson(err.response().errorBody()?.string(), ErrorMessage::class.java).message)
        } else {
            onError(err.message)
        }
    }
}