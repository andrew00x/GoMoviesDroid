package com.andrew00x.gomoviesdroid.catalog

import android.os.Parcel
import android.os.Parcelable
import com.andrew00x.gomoviesdroid.GomoviesService
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

data class Movie(
    val id: Int,
    val title: String,
    val file: String,
    val drive: String,
    val available: Boolean,
    val detailsAvailable: Boolean) : Parcelable {

  override fun writeToParcel(dest: Parcel, flags: Int) {
    dest.writeInt(id)
    dest.writeString(title)
    dest.writeString(file)
    dest.writeString(drive)
    dest.writeInt(if (available) 1 else 0)
    dest.writeInt(if (detailsAvailable) 1 else 0)
  }

  override fun describeContents(): Int = 0

  companion object CREATOR : Parcelable.Creator<Movie> {
    override fun createFromParcel(parcel: Parcel): Movie {
      return Movie(
          parcel.readInt(),
          parcel.readString(),
          parcel.readString(),
          parcel.readString(),
          parcel.readInt() == 1,
          parcel.readInt() == 1
      )
    }

    override fun newArray(size: Int): Array<Movie?> {
      return arrayOfNulls(size)
    }
  }
}

data class MovieDetails(
    val originalTitle: String,
    val overview: String,
    val genres: List<String>,
    val releaseDate: String,
    val budget: Long,
    val revenue: Long,
    val runtime: Int,
    val posterUrl: String) : Parcelable {

  override fun writeToParcel(dest: Parcel, flags: Int) {
    dest.writeString(originalTitle)
    dest.writeString(overview)
    dest.writeStringList(genres)
    dest.writeString(releaseDate)
    dest.writeLong(budget)
    dest.writeLong(revenue)
    dest.writeString(posterUrl)
  }

  override fun describeContents(): Int = 0

  companion object CREATOR : Parcelable.Creator<MovieDetails> {
    override fun createFromParcel(parcel: Parcel): MovieDetails {
      return MovieDetails(
          parcel.readString(),
          parcel.readString(),
          parcel.createStringArrayList(),
          parcel.readString(),
          parcel.readLong(),
          parcel.readLong(),
          parcel.readInt(),
          parcel.readString()
      )
    }

    override fun newArray(size: Int): Array<MovieDetails?> {
      return arrayOfNulls(size)
    }
  }
}

@Singleton
class CatalogModel @Inject constructor(private val service: GomoviesService) {
  // tag can be title, genre or other info about movie that is indexed my server
  fun load(tag: String = ""): Single<List<Movie>> {
    return (if (tag.isBlank()) service.list() else service.search(tag.trim()))
        .map { listData ->
          listData.map { data ->
            Movie(id = data.id, title = data.title, file = data.file, drive = data.drive, available = data.available, detailsAvailable = data.detailsAvailable)
          }
        }
  }

  fun loadDetails(id: Int, lang: String): Single<MovieDetails> = service.details(id, lang).map { MovieDetails(originalTitle = it.originalTitle, overview = it.overview, genres = it.genres, releaseDate = it.releaseDate, budget = it.budget, revenue = it.revenue, runtime = it.runtime, posterUrl = it.posterLargeUrl) }
}
