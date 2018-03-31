package com.andrew00x.gomoviesdroid.main

import android.support.v4.view.ViewPager
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.MainThreadDisposable

class MainModel(private val pager: ViewPager) {

    fun changePage(): Observable<Int> {
        return PageChangeObservable(pager)
    }

    private class PageChangeObservable(private val pager: ViewPager) : Observable<Int>() {
        override fun subscribeActual(observer: Observer<in Int>) {
            val listener = PageChangeListener(pager, observer)
            pager.addOnPageChangeListener(listener)
            observer.onSubscribe(listener)
        }
    }

    private class PageChangeListener(private val pager: ViewPager, private val observer: Observer<in Int>) : MainThreadDisposable(), ViewPager.OnPageChangeListener {
        override fun onDispose() {
            pager.removeOnPageChangeListener(this)
        }

        override fun onPageScrollStateChanged(state: Int) {}

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

        override fun onPageSelected(position: Int) {
            if (!isDisposed) {
                observer.onNext(position)
            }
        }
    }
}
