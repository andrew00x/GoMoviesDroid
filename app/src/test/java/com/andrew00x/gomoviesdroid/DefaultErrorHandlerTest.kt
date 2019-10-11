package com.andrew00x.gomoviesdroid

import com.andrew00x.gomoviesdroid.ui.BaseView
import com.google.gson.GsonBuilder
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import org.mockito.Mockito
import retrofit2.HttpException
import retrofit2.Response.error

@RunWith(Parameterized::class)
class DefaultErrorHandlerTest(private val error: Throwable, private val message: String?) {
  companion object {
    @JvmStatic
    @Parameters
    fun data(): Collection<Array<out Any?>> {
      return listOf(
          arrayOf(Exception("failed"),
              "failed"),
          arrayOf(HttpException(error<String>(400, ResponseBody.create(MediaType.parse("application/json"), "{\"message\":\"failed\"}"))),
              "failed"),
          arrayOf(HttpException(error<String>(400, ResponseBody.create(MediaType.parse("text/plain"), "failed"))),
              "failed"),
          arrayOf(Exception(),
              null)
      )
    }
  }

  private lateinit var errorHandler: DefaultErrorHandler
  private lateinit var view: BaseView

  @Before
  fun setup() {
    val gson = GsonBuilder().create()
    errorHandler = DefaultErrorHandler(gson)
    view = Mockito.mock(BaseView::class.java)
  }

  @Test
  fun handleError() {
    errorHandler.handleError(view, error)
    Mockito.verify(view).showError(message)
  }
}