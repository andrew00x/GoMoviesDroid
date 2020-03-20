package com.andrew00x.gomoviesdroid.ui.torrent

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.andrew00x.gomoviesdroid.R
import com.andrew00x.gomoviesdroid.TorrentDownload
import com.andrew00x.gomoviesdroid.torrent.TorrentDownloadItem
import io.reactivex.subjects.Subject

class TorrentAdapter(
    context: Context,
    objects: MutableList<TorrentDownloadItem>,
    private val onDeleteItem: Subject<TorrentDownloadItem>
) : ArrayAdapter<TorrentDownloadItem>(context, 0, objects) {
  data class TorrentDownloadItemViewHolder(
      var title: TextView,
      var progress: TextView,
      var progressBar: ProgressBar,
      var remove: ImageButton
  )

  override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
    val rowViewHolder: TorrentDownloadItemViewHolder
    val rowView: View
    if (convertView == null) {
      rowView = LayoutInflater.from(context).inflate(R.layout.torrent_row, null)
      rowViewHolder = TorrentDownloadItemViewHolder(
          title = rowView.findViewById(R.id.torrent_row_title),
          progress = rowView.findViewById(R.id.torrent_row_progress),
          progressBar = rowView.findViewById(R.id.torrent_row_progress_bar),
          remove = rowView.findViewById(R.id.torrent_row_remove)
      )
      rowViewHolder.remove.setOnClickListener { v ->
        onDeleteItem.onNext(v.tag as TorrentDownloadItem)
      }
      rowView.tag = rowViewHolder
    } else {
      rowView = convertView
      rowViewHolder = rowView.tag as TorrentDownloadItemViewHolder
    }
    val torrent = getItem(position)
    val torrentData = torrent.data
    rowViewHolder.title.text = displayString(torrentData)
    rowViewHolder.title.isActivated = !torrentData.stopped
    val progress  = ((torrentData.completedSize * 100) / torrentData.size).toInt()
    rowViewHolder.progress.text = "%d%%".format(progress)
    rowViewHolder.progressBar.max = 100
    rowViewHolder.progressBar.progress = progress
    rowViewHolder.remove.tag = torrent
    return rowView
  }

  private fun displayString(torrent: TorrentDownload): String {
    val status = context.resources.getText(if (torrent.stopped) R.string.torrent_list_stopped else R.string.torrent_list_active)
    return """
      ${torrent.name}
      $status
    """.trimIndent()
  }
}
