package com.andrew00x.gomoviesdroid

import io.reactivex.Observable
import io.reactivex.Observer

class Event<T> : Observable<T>() {
  private var observers = arrayListOf<Observer<in T>>()
  override fun subscribeActual(observer: Observer<in T>?) {
    this.observers.add(observer!!)
  }

  fun send(value: T) {
    observers.forEach { observer -> observer.onNext(value) }
  }
}