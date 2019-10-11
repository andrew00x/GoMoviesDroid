package com.andrew00x.gomoviesdroid.ui.catalog

import android.view.View
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import com.andrew00x.gomoviesdroid.R

class MovieDetailsViewHolder (view: View){
  var languages: Spinner = view.findViewById(R.id.movie_details_languages)
  val poster: ImageView = view.findViewById(R.id.movie_details_poster)
  val title: TextView = view.findViewById(R.id.movie_details_title)
  val releaseDate: TextView = view.findViewById(R.id.movie_details_release_date)
  val runtime: TextView = view.findViewById(R.id.movie_details_runtime)
  val genres: TextView = view.findViewById(R.id.movie_details_genres)
  val overview: TextView = view.findViewById(R.id.movie_details_overview)
  val budget: TextView = view.findViewById(R.id.movie_details_budget)
  val revenue: TextView = view.findViewById(R.id.movie_details_revenue)
}
