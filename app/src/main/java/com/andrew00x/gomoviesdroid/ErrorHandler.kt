package com.andrew00x.gomoviesdroid

import com.andrew00x.gomoviesdroid.ui.BaseView
import com.google.gson.Gson
import retrofit2.HttpException

abstract class ErrorHandler {
  fun handleError(view: BaseView, e: Throwable) {
    view.showError(getMessage(e))
  }

  protected abstract fun getMessage(e: Throwable): String?
}

class DefaultErrorHandler(private val gson: Gson) : ErrorHandler() {
  private data class ErrorMessage(var message: String)

  override fun getMessage(e: Throwable): String? = when (e) {
    is HttpException -> {
      val response = e.response().errorBody()
      val contentType = response?.contentType()
      if (contentType != null && contentType.type() == "application" && contentType.subtype() == "json")
        gson.fromJson(response.string(), ErrorMessage::class.java).message
      else if (response != null)
        response.string()
      else
        e.message
    }
    else -> e.message
  }
}