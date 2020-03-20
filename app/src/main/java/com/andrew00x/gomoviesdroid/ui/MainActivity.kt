package com.andrew00x.gomoviesdroid.ui

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.andrew00x.gomoviesdroid.GomoviesApplication
import com.andrew00x.gomoviesdroid.R
import com.andrew00x.gomoviesdroid.config.Configuration
import com.andrew00x.gomoviesdroid.config.ConfigurationListener
import com.andrew00x.gomoviesdroid.ui.catalog.CatalogFragment
import com.andrew00x.gomoviesdroid.ui.config.ConfigurationFragment
import com.andrew00x.gomoviesdroid.ui.player.PlayerFragment
import com.andrew00x.gomoviesdroid.ui.playlist.PlaylistListFragment
import com.andrew00x.gomoviesdroid.ui.queue.QueueFragment
import com.andrew00x.gomoviesdroid.ui.torrent.TorrentFragment

class MainActivity : AppCompatActivity(), BaseView, ConfigurationListener {
  private val pages = mutableListOf<BaseFragment>()
  private val errorView by lazy { layoutInflater.inflate(R.layout.error_message, null) }
  private val infoView by lazy { layoutInflater.inflate(R.layout.info_message, null) }
  private lateinit var pager: ViewPager
  private lateinit var progressSpinner: ProgressBar

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    pager = findViewById(R.id.fragment_container)
    progressSpinner = findViewById(R.id.progress_spinner)
    setupPager()
  }

  private fun setupPager() {
    pages.clear()
    pages.add(CatalogFragment.newInstance())
    pages.add(QueueFragment.newInstance())
    pages.add(PlaylistListFragment.newInstance())
    pages.add(PlayerFragment.newInstance())
    pages.add(TorrentFragment.newInstance())
    pages.add(ConfigurationFragment.newInstance())
    pager.adapter = object : FragmentStatePagerAdapter(supportFragmentManager) {
      override fun getItem(position: Int): Fragment = pages[position]
      override fun getCount(): Int = pages.size
    }
    pager.addOnPageChangeListener(object: ViewPager.SimpleOnPageChangeListener() {
      override fun onPageSelected(position: Int) {
        pages[position].refresh()
      }
    })

    val tabs = pager.findViewById<TabLayout>(R.id.fragment_container_tabs)
    tabs.setupWithViewPager(pager)
    tabs.getTabAt(0)?.customView = layoutInflater.inflate(R.layout.pager_tab_movie_list, null)
    tabs.getTabAt(1)?.customView = layoutInflater.inflate(R.layout.pager_tab_play_queue, null)
    tabs.getTabAt(2)?.customView = layoutInflater.inflate(R.layout.pager_tab_playlists, null)
    tabs.getTabAt(3)?.customView = layoutInflater.inflate(R.layout.pager_tab_player, null)
    tabs.getTabAt(4)?.customView = layoutInflater.inflate(R.layout.pager_tab_torrent, null)
    tabs.getTabAt(5)?.customView = layoutInflater.inflate(R.layout.pager_tab_settings, null)
  }

  override fun recreate() {
    (application as GomoviesApplication).refreshComponent()
    super.recreate()
  }

  override fun showInfo(message: String) {
    val toast = Toast(this)
    infoView.findViewById<TextView>(R.id.info_message).text = message
    toast.view = infoView
    toast.duration = Toast.LENGTH_LONG
    toast.show()
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

  override fun afterSave(config: Configuration) {
    recreate()
  }
}
