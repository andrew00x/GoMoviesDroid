package com.andrew00x.gomoviesdroid.catalog

import com.andrew00x.gomoviesdroid.GomoviesService
import com.andrew00x.gomoviesdroid.Movie
import io.reactivex.Single
import javax.inject.Inject

class CatalogModel @Inject constructor(private val service: GomoviesService) {
  fun search(title: String): Single<List<Movie>> {
    return service.search(title)
  }

  fun list(): Single<List<Movie>> {
    return service.list()
  }
}
