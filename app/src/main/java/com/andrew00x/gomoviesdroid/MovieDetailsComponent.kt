package com.andrew00x.gomoviesdroid

import com.andrew00x.gomoviesdroid.ui.catalog.MovieDetailsFragment
import dagger.Subcomponent
import javax.inject.Scope

@Scope
@Retention
annotation class MovieDetailsScope

@MovieDetailsScope
@Subcomponent(modules = [MovieDetailsModule::class])
interface MovieDetailsComponent {
  fun inject(fr: MovieDetailsFragment)
}