package com.andrew00x.gomoviesdroid.playlist

import android.os.Parcel
import android.os.Parcelable
import com.activeandroid.Model
import com.activeandroid.annotation.Column
import com.activeandroid.annotation.Table
import com.activeandroid.query.Select
import com.andrew00x.gomoviesdroid.catalog.Movie
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistModel @Inject constructor() {
  companion object {
    private const val NEW_PLAYLIST = "New Playlist"
  }

  fun getAll(): List<Playlist> = Select().from(Playlist::class.java).execute()

  fun getAll(playlist: Playlist): List<PlaylistItem> =
      Select().from(PlaylistItem::class.java).where("playlist = ?", playlist.id).execute()

  fun delete(playlist: Playlist): List<Playlist> {
    playlist.delete()
    return getAll()
  }

  fun delete(playlist: Playlist, item: PlaylistItem): List<PlaylistItem> {
    item.delete()
    return getAll(playlist)
  }

  fun save(playlist: Playlist): Playlist {
    check(playlist.save() != -1L) { "Unable save playlist" }
    return playlist
  }

  fun create(): Playlist {
    val l = Select().from(Playlist::class.java)
        .where("name glob ?", "$NEW_PLAYLIST [0-9]*")
        .orderBy("id desc")
        .limit(1)
        .execute<Playlist>()
    val idx = if (l.isEmpty()) 1 else {
      l[0].name.substringAfter("$NEW_PLAYLIST ").toInt() + 1
    }
    return create("$NEW_PLAYLIST $idx")
  }

  fun create(title: String): Playlist = save(Playlist(title))

  fun addItem(playlist: Playlist, moviePath: String): PlaylistItem {
    val item = PlaylistItem(playlist, moviePath)
    check(item.save() != -1L) { "Unable save playlist item" }
    return item
  }

  fun addItem(playlist: Playlist, movie: Movie): PlaylistItem = addItem(playlist, movie.file)
}

@Table(name = "playlist")
data class Playlist(
    @Column(name = "name", unique = true) var name: String = ""
) : Model(), Parcelable {

  override fun writeToParcel(dest: Parcel, flags: Int) {
    dest.writeString(name)
  }

  override fun describeContents(): Int = 0

  companion object CREATOR : Parcelable.Creator<Playlist> {
    override fun createFromParcel(source: Parcel): Playlist {
      val movies = mutableListOf<PlaylistItem>()
      source.readList(movies, Playlist::class.java.classLoader)
      return Playlist(source.readString())
    }

    override fun newArray(size: Int): Array<Playlist?> = arrayOfNulls(size)
  }
}

@Table(name = "playlist_item")
data class PlaylistItem(
    @Column(name = "playlist", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE) var playlist: Playlist? = null,
    @Column(name = "file") var file: String = ""
) : Model(), Parcelable {
  val title: String
    get() = file.substringAfterLast('/')

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeString(file)
  }

  override fun describeContents(): Int = 0

  companion object CREATOR : Parcelable.Creator<PlaylistItem> {
    override fun createFromParcel(source: Parcel): PlaylistItem = PlaylistItem(file = source.readString())

    override fun newArray(size: Int): Array<PlaylistItem?> = arrayOfNulls(size)
  }
}
