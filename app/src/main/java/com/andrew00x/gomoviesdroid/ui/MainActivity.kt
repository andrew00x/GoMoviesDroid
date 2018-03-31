package com.andrew00x.gomoviesdroid.ui

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.andrew00x.gomoviesdroid.BaseView
import com.andrew00x.gomoviesdroid.R
import com.andrew00x.gomoviesdroid.main.MainModel
import com.andrew00x.gomoviesdroid.main.MainPresenter
import com.andrew00x.gomoviesdroid.main.MainView
import com.andrew00x.gomoviesdroid.main.ViewPage

class MainActivity : MainView, AppCompatActivity() {
    private val pages = mutableListOf<Fragment>()
    private lateinit var pager: ViewPager
    private lateinit var progressSpinner: ProgressBar
    private val errorView by lazy { layoutInflater.inflate(R.layout.error_message, null) }

    private var presenter: MainPresenter? = null

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

            override fun getItemPosition(item: Any?): Int {
                return if (pages.indexOf(item) < 0)
                    PagerAdapter.POSITION_NONE
                else super.getItemPosition(item)
            }
        }

        val model = MainModel(pager)
        val presenter = MainPresenter(model, this)
        presenter.start()
        this.presenter = presenter

        showPage(ViewPage.CATALOG)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.stop()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        refresh()
    }

    override fun showPage(page: ViewPage) {
        pager.currentItem = page.position
    }

    override fun removePage(page: ViewPage) {
        if (pages.size > page.position) {
            pages.removeAt(page.position)
            pager.adapter.notifyDataSetChanged()
        }
    }

    override fun showError(message: String?) {
        val toast = Toast(this)
        errorView.findViewById<TextView>(R.id.error_message).text = message ?: resources.getText(R.string.default_error_message)
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

    override fun hideKeyboard() {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(pager.windowToken, 0)
    }

    override fun refresh() {
        hideKeyboard()
        val fragment = pages[pager.currentItem]
        (fragment as BaseView).refresh()
    }
}
