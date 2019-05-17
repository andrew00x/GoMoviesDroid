package com.andrew00x.gomoviesdroid.config

import io.reactivex.Observable

interface ConfigurationEventSource {
  fun changeServer(): Observable<String>
  fun changePort(): Observable<String>
  fun clickOnSaveConfiguration(): Observable<Any>
}