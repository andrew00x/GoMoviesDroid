package com.andrew00x.gomoviesdroid.ui.queue

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import com.andrew00x.gomoviesdroid.R
import com.andrew00x.gomoviesdroid.queue.QueueItem
import io.reactivex.subjects.Subject

class QueueAdapter(
    context: Context,
    objects: MutableList<QueueItem>,
    private val onDeleteItem: Subject<QueueItem>
) : ArrayAdapter<QueueItem>(context, 0, objects) {
  data class QueueItemViewHolder(
      val title: TextView,
      val remove: ImageButton
  )

  override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
    val rowViewHolder: QueueItemViewHolder
    val rowView: View
    if (convertView == null) {
      rowView = LayoutInflater.from(context).inflate(R.layout.queue_row, null)
      rowViewHolder = QueueItemViewHolder(
          rowView.findViewById(R.id.queue_row_movie_title),
          rowView.findViewById(R.id.queue_row_remove)
      )
      rowViewHolder.remove.setOnClickListener { v ->
        onDeleteItem.onNext(v.tag as QueueItem)
      }
      rowView.tag = rowViewHolder
    } else {
      rowView = convertView
      rowViewHolder = rowView.tag as QueueItemViewHolder
    }
    val movie = getItem(position)
    rowViewHolder.remove.tag = movie
    rowViewHolder.title.text = movie.moviePath.substringAfterLast('/')
    return rowView
  }
}
