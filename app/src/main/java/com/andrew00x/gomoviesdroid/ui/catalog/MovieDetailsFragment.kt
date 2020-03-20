package com.andrew00x.gomoviesdroid.ui.catalog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.andrew00x.gomoviesdroid.GomoviesApplication
import com.andrew00x.gomoviesdroid.MovieDetailsModule
import com.andrew00x.gomoviesdroid.R
import com.andrew00x.gomoviesdroid.catalog.*
import com.andrew00x.gomoviesdroid.ui.BaseDialogFragment
import com.jakewharton.rxbinding2.widget.RxAdapterView
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import java.text.DecimalFormat
import javax.inject.Inject

class MovieDetailsFragment : BaseDialogFragment(), MovieDetailsView {
  companion object {
    const val ARG = "Movie"
    const val TAG = "MovieDetailsContent"

    fun newInstance(movie: Movie): MovieDetailsFragment {
      val args = Bundle()
      args.putParcelable(ARG, movie)
      val fr = MovieDetailsFragment()
      fr.arguments = args
      return fr
    }
  }

  @Inject lateinit var presenter: MovieDetailsPresenter
  private lateinit var movie: Movie
  private lateinit var components: MovieDetailsViewHolder
  private lateinit var languagesData: ArrayAdapter<String>

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    movie = arguments!!.getParcelable(ARG)!!
    (activity!!.application as GomoviesApplication).component.plus(MovieDetailsModule(movie)).inject(this)
    inFullScreen()
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.fragment_movie_details, container, false)!!
    components = MovieDetailsViewHolder(view)
    languagesData = ArrayAdapter(activity!!, R.layout.movie_details_lang_row, mutableListOf())
    languagesData.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    components.languages.adapter = languagesData
    return view
  }

  override fun onResume() {
    super.onResume()
    presenter.attach(this)
  }

  override fun onPause() {
    presenter.detach()
    super.onPause()
  }

  override fun setLanguages(langs: Set<String>) {
    languagesData.clear()
    languagesData.addAll(langs)
  }

  override fun setDetails(details: MovieDetails, lang: String) {
    Picasso.get().load(details.posterUrl).placeholder(R.drawable.ic_file_download_24dp).error(R.drawable.ic_error_24dp).into(components.poster)
    components.title.text = details.originalTitle
    components.releaseDate.text = details.releaseDate
    if (details.runtime > 0) components.runtime.text = formatTime(details.runtime)
    components.genres.text = details.genres.joinToString()
    components.overview.text = details.overview
    if (details.budget > 0) {
      components.budget.text = formatMoney(details.budget)
      components.budgetView.visibility = View.VISIBLE
    }
    if (details.revenue > 0) {
      components.revenue.text = formatMoney(details.revenue)
      components.revenueView.visibility = View.VISIBLE
    }
  }

  override fun changeLanguage(): Observable<String> {
    return RxAdapterView.itemSelections(components.languages)
        .map { pos -> if (pos == AdapterView.INVALID_POSITION) "" else languagesData.getItem(pos) }
  }

  private fun formatTime(min: Int): String {
    val h = min / 60
    val m = min % 60
    return "%02dh %02dm".format(h, m)
  }

  private fun formatMoney(amountUSD: Long): String = "\$${DecimalFormat("#,###.00").format(amountUSD)}"
}
