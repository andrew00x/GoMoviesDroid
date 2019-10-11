package com.andrew00x.gomoviesdroid.ui.playlist

import android.widget.EditText
import com.andrew00x.gomoviesdroid.ui.BaseView
import com.andrew00x.gomoviesdroid.playlist.Playlist
import com.andrew00x.gomoviesdroid.playlist.PlaylistNameView
import io.reactivex.Observable

class InlineNameEditDialog(
    private val owner: BaseView,
    private val name: EditText,
    private val save: Observable<Playlist>
) : PlaylistNameView {
  override fun setName(name: String) = this.name.setText(name)

  override fun getName(): String = name.text.toString()

  override fun clickOnSave(): Observable<Any> = save.map { Unit }

  override fun showLoader() = owner.showLoader()

  override fun hideLoader() = owner.hideLoader()

  override fun showInfo(message: String) = owner.showInfo(message)

  override fun showError(message: String?) = owner.showError(message)
}
