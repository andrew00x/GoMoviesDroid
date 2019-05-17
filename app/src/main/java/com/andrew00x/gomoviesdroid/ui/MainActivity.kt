package com.andrew00x.gomoviesdroid.ui

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.andrew00x.gomoviesdroid.BaseView
import com.andrew00x.gomoviesdroid.GomoviesApplication
import com.andrew00x.gomoviesdroid.PlaybackListener
import com.andrew00x.gomoviesdroid.R
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

class MainActivity : PlaybackListener, BaseView, AppCompatActivity() {
  private val pages = mutableListOf<Fragment>()
  private val subscriptions: CompositeDisposable = CompositeDisposable()
  private val errorView by lazy { layoutInflater.inflate(R.layout.error_message, null) }
  private lateinit var pager: ViewPager
  private lateinit var progressSpinner: ProgressBar

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    pager = findViewById(R.id.fragment_container)
    progressSpinner = findViewById(R.id.progress_spinner)

    pages.clear()
    pages.add(ViewPage.CONFIGURATION.position, ConfigurationFragment())
    pages.add(ViewPage.CATALOG.position, CatalogFragment())
    pages.add(ViewPage.PLAYER.position, PlayerFragment())
    pager.adapter = object : FragmentStatePagerAdapter(supportFragmentManager) {
      override fun getItem(position: Int): Fragment {
        return pages[position]
      }

      override fun getCount(): Int {
        return pages.size
      }
    }

    subscriptions.add(changePage().subscribe { refresh() })

    showPage(ViewPage.CATALOG)
  }

  override fun onDestroy() {
    super.onDestroy()
    subscriptions.dispose()
  }

  override fun recreate() {
    (application as GomoviesApplication).refreshComponent()
    super.recreate()
  }

  private fun changePage(): Observable<Int> {
    return Observable.create<Int> { emitter ->
      val listener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {}

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

        override fun onPageSelected(position: Int) {
          emitter.onNext(position)
        }
      }
      emitter.setCancellable { pager.removeOnPageChangeListener(listener) }
      pager.addOnPageChangeListener(listener)
    }
  }

  override fun onWindowFocusChanged(hasFocus: Boolean) {
    refresh()
  }

  override fun showError(message: String?) {
    val toast = Toast(this)
    errorView.findViewById<TextView>(R.id.error_message).text = (message
        ?: resources.getText(R.string.default_error_message))
    toast.view = errorView
    toast.duration = Toast.LENGTH_LONG
    toast.show()
  }

  override fun showLoader() {
    progressSpinner.visibility = View.VISIBLE
  }

  override fun hideLoader() {
    progressSpinner.visibility = View.GONE
  }

  override fun onPlaybackStarted() {
    showPage(ViewPage.PLAYER)
  }

  override fun onPlaybackStopped() {
    showPage(ViewPage.CATALOG)
  }

  override fun refresh() {
    hideKeyboard()
    val fragment = pages[pager.currentItem]
    if (fragment.isAdded) (fragment as BaseView).refresh()
  }

  private fun showPage(page: ViewPage) {
    pager.currentItem = page.position
  }

  private fun hideKeyboard() {
    (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(pager.windowToken, 0)
  }
}
