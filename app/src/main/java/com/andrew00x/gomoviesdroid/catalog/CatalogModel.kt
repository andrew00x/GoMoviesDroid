package com.andrew00x.gomoviesdroid.catalog

import android.view.View
import android.widget.EditText
import android.widget.ListView
import com.andrew00x.gomoviesdroid.GomoviesService
import com.andrew00x.gomoviesdroid.Movie
import com.andrew00x.gomoviesdroid.PlayerStatus
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable

class CatalogModel(private val service: GomoviesService,
                   private val searchTitleText: EditText,
                   private val clearSearchButton: View,
                   private val movies: ListView) {

    fun changeSearchField(): Observable<String> {
        return RxTextView.textChanges(searchTitleText).map { title -> title.toString() }
    }

    fun clickClearSearchButton(): Observable<Any> {
        return RxView.clicks(clearSearchButton)
    }

    fun clickMovie(): Observable<Movie> {
        return Observable.create<Movie> { emitter ->
            emitter.setCancellable { movies.onItemClickListener = null }
            movies.setOnItemClickListener { parent, _, position, _ ->
                emitter.onNext(parent.adapter.getItem(position) as Movie)
            }
        }
    }

    fun searchMovies(title: String): Observable<List<Movie>> {
        return service.search(title)
    }

    fun listMovies(): Observable<List<Movie>> {
        return service.list()
    }

    fun playMovie(movie: Movie): Observable<PlayerStatus> {
        return service.play(movie)
    }
}
