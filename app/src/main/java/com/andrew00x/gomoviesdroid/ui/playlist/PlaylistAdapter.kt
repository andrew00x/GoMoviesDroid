package com.andrew00x.gomoviesdroid.ui.playlist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.andrew00x.gomoviesdroid.R
import com.andrew00x.gomoviesdroid.playlist.PlaylistItem
import io.reactivex.subjects.Subject

class PlaylistAdapter(
    context: Context,
    objects: MutableList<PlaylistItem>,
    private val onDeleteItem: Subject<PlaylistItem>
) : ArrayAdapter<PlaylistItem>(context, 0, objects) {
  data class PlaylistItemViewHolder(
      val movie: TextView,
      val delete: View
  )

  override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
    val rowViewHolder: PlaylistItemViewHolder
    var rowView: View? = convertView
    if (rowView == null) {
      rowView = LayoutInflater.from(context).inflate(R.layout.playlist_row, null)
      rowViewHolder = PlaylistItemViewHolder(
          rowView.findViewById(R.id.playlist_row_movie_title),
          rowView.findViewById(R.id.playlist_row_delete)
      )
      rowViewHolder.delete.setOnClickListener { v ->
        val item = v.tag as PlaylistItem?
        if (item != null) onDeleteItem.onNext(item)
      }
      rowView.tag = rowViewHolder
    } else {
      rowViewHolder = rowView.tag as PlaylistItemViewHolder
    }
    val playlistItem = getItem(position)
    rowViewHolder.movie.text = playlistItem?.title
    rowViewHolder.delete.tag = playlistItem
    return rowView!!
  }
}
