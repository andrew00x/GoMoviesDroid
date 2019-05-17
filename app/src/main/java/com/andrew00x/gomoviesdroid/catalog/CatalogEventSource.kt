package com.andrew00x.gomoviesdroid.catalog

import com.andrew00x.gomoviesdroid.Movie
import io.reactivex.Observable

interface CatalogEventSource {
  fun changeSearchField(): Observable<String>
  fun clickClearSearchField(): Observable<Any>
  fun clickMovie(): Observable<Movie>
}