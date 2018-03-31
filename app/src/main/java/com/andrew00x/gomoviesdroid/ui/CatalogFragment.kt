package com.andrew00x.gomoviesdroid.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import com.andrew00x.gomoviesdroid.*
import com.andrew00x.gomoviesdroid.catalog.CatalogModel
import com.andrew00x.gomoviesdroid.catalog.CatalogPresenter
import com.andrew00x.gomoviesdroid.catalog.CatalogView
import javax.inject.Inject

class CatalogFragment : CatalogView, BaseFragment() {
    @Inject lateinit var service: GomoviesService

    private lateinit var searchField: EditText
    private lateinit var clearSearchButton: ImageButton
    private lateinit var moviesList: ArrayAdapter<Movie>

    private var presenter: CatalogPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity.application as GomoviesApplication).component.injectInto(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater?.inflate(R.layout.fragment_catalog, container, false)!!

        val moviesListView = view.findViewById<ListView>(R.id.movies_list)
        moviesList = MoviesList(activity, 0, mutableListOf())
        moviesListView.adapter = moviesList
        searchField = view.findViewById(R.id.search_field)
        clearSearchButton = view.findViewById(R.id.clear_search_field_button)

        val model = CatalogModel(service, searchField, clearSearchButton, moviesListView)
        val presenter = CatalogPresenter(model, this)
        presenter.start()
        this.presenter = presenter

        return view
    }

    override fun getPresenter(): BasePresenter? {
        return presenter
    }

    override fun showMovies(movies: List<Movie>) {
        moviesList.clear()
        moviesList.addAll(movies)
        moviesList.notifyDataSetChanged()
    }

    override fun showClearSearchButton() {
        clearSearchButton.visibility = View.VISIBLE
    }

    override fun hideClearSearchButton() {
        clearSearchButton.visibility = View.INVISIBLE
    }

    override fun clearSearchField() {
        searchField.setText("")
    }

    override fun onPlaybackStarted() {
        if (activity is PlaybackListener) (activity as PlaybackListener).onPlaybackStarted()
    }

    override fun onPlaybackStopped() {
        if (activity is PlaybackListener) (activity as PlaybackListener).onPlaybackStopped()
    }
}
