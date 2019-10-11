package com.andrew00x.gomoviesdroid

import okhttp3.HttpUrl

interface UrlValidator {
  fun validate(url: String): Boolean
}

class DefaultUrlValidator : UrlValidator {
  override fun validate(url: String): Boolean = HttpUrl.parse(url) != null
}
