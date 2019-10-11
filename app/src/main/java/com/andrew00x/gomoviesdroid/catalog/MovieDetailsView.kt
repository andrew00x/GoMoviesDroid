package com.andrew00x.gomoviesdroid.catalog

import com.andrew00x.gomoviesdroid.ui.BaseView
import io.reactivex.Observable

interface MovieDetailsView : BaseView {
  fun setLanguages(langs: Set<String>)
  fun setDetails(details: MovieDetails, lang: String)
  fun changeLanguage(): Observable<String>
}