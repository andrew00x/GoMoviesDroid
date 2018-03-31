package com.andrew00x.gomoviesdroid.ui

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.andrew00x.gomoviesdroid.Movie
import com.andrew00x.gomoviesdroid.R

class MoviesList(context: Context?, resource: Int, objects: MutableList<Movie>) : ArrayAdapter<Movie>(context, resource, objects) {
    data class MovieViewHolder(val title: TextView)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowViewHolder: MovieViewHolder
        val rowView: View
        if (convertView == null) {
            rowView = LayoutInflater.from(context).inflate(R.layout.movies_row, null)
            rowViewHolder = MovieViewHolder(rowView.findViewById(R.id.movie_title))
            rowView.tag = rowViewHolder
        } else {
            rowView = convertView
            rowViewHolder = rowView.tag as MovieViewHolder
        }
        val movie = getItem(position)
        rowViewHolder.title.tag = movie
        rowViewHolder.title.text = movie.title
        rowViewHolder.title.setTextColor(if (movie.available) Color.LTGRAY else Color.GRAY)
        return rowView
    }
}
