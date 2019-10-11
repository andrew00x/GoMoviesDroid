package com.andrew00x.gomoviesdroid.ui.catalog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.andrew00x.gomoviesdroid.R
import com.andrew00x.gomoviesdroid.catalog.Movie

class MovieListAdapter(context: Context, objects: MutableList<Movie>) : ArrayAdapter<Movie>(context, 0, objects) {
  data class MovieViewHolder(val title: TextView)

  override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
    val rowViewHolder: MovieViewHolder
    val rowView: View
    if (convertView == null) {
      rowView = LayoutInflater.from(context).inflate(R.layout.catalog_row, null)
      rowViewHolder = MovieViewHolder(rowView.findViewById(R.id.catalog_row_movie_title))
      rowView.tag = rowViewHolder
    } else {
      rowView = convertView
      rowViewHolder = rowView.tag as MovieViewHolder
    }
    val movie = getItem(position)
    if (movie != null) {
      rowViewHolder.title.tag = movie
      rowViewHolder.title.text = movie.title
      rowViewHolder.title.isEnabled = movie.available
    }
    return rowView
  }
}
