package com.andrew00x.gomoviesdroid.ui.playlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import com.andrew00x.gomoviesdroid.R
import com.andrew00x.gomoviesdroid.playlist.Playlist
import com.andrew00x.gomoviesdroid.playlist.PlaylistNamePresenter
import com.andrew00x.gomoviesdroid.ui.MainActivity
import io.reactivex.subjects.Subject

class PlaylistListAdapter(
    context: MainActivity,
    objects: MutableList<Playlist>,
    private val presenter: PlaylistNamePresenter,
    private val onEditItem: Subject<Playlist>,
    private val onSaveItem: Subject<Playlist>,
    private val onDeleteItem: Subject<Playlist>
) : ArrayAdapter<Playlist>(context, 0, objects) {

  data class PlaylistListItemNormalViewHolder(
      val title: TextView,
      val edit: View,
      val delete: View
  )

  data class PlaylistListItemEditViewHolder(
      val titleEdit: EditText,
      val save: View
  )

  companion object {
    private const val NORMAL_VIEW = 0
    private const val EDIT_VIEW = 1
  }

  var updating: Int = -1

  override fun getItemViewType(position: Int): Int = if (position == updating) EDIT_VIEW else NORMAL_VIEW

  override fun getViewTypeCount(): Int = 2

  override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
    return if (getItemViewType(position) == NORMAL_VIEW) {
      getNormalView(position, convertView)
    } else {
      getEditView(position, convertView)
    }
  }

  private fun getNormalView(position: Int, convertView: View?): View {
    val rowViewHolder: PlaylistListItemNormalViewHolder
    var rowView: View? = convertView
    if (rowView == null) {
      rowView = LayoutInflater.from(context).inflate(R.layout.playlist_list_row, null)
      rowViewHolder = PlaylistListItemNormalViewHolder(
          title = rowView.findViewById(R.id.playlist_list_row_playlist_title),
          edit = rowView.findViewById(R.id.playlist_list_row_edit),
          delete = rowView.findViewById(R.id.playlist_list_row_delete)
      )
      rowViewHolder.edit.setOnClickListener { v ->
        onEditItem.onNext(v.tag as Playlist)
      }
      rowViewHolder.delete.setOnClickListener { v ->
        onDeleteItem.onNext(v.tag as Playlist)
      }
      rowView.tag = rowViewHolder
    } else {
      rowViewHolder = rowView.tag as PlaylistListItemNormalViewHolder
    }
    val playlist = getItem(position)
    rowViewHolder.edit.tag = playlist
    rowViewHolder.delete.tag = playlist
    rowViewHolder.title.text = playlist.name
    return rowView!!
  }

  private fun getEditView(position: Int, convertView: View?): View {
    val rowViewHolder: PlaylistListItemEditViewHolder
    var rowView: View? = convertView
    if (rowView == null) {
      rowView = LayoutInflater.from(context).inflate(R.layout.playlist_list_row_edit, null)
      rowViewHolder = PlaylistListItemEditViewHolder(
          titleEdit = rowView.findViewById(R.id.playlist_list_row_playlist_title_edit),
          save = rowView.findViewById(R.id.playlist_list_row_save)
      )
      rowView.tag = rowViewHolder
      rowViewHolder.save.setOnClickListener { v ->
        onSaveItem.onNext(v.tag as Playlist)
      }
    } else {
      rowViewHolder = rowView.tag as PlaylistListItemEditViewHolder
    }
    presenter.detach()
    val playlist = getItem(position)
    if (playlist != null) {
      val editNameView = InlineNameEditDialog(context as MainActivity, rowViewHolder.titleEdit, onSaveItem)
      presenter.playlist = playlist
      presenter.attach(editNameView)
      rowViewHolder.save.tag = playlist
    }
    return rowView!!
  }

}
