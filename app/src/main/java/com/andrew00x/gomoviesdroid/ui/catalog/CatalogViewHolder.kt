package com.andrew00x.gomoviesdroid.ui.catalog

import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import com.andrew00x.gomoviesdroid.R

class CatalogViewHolder(view: View) {
  val searchField: EditText = view.findViewById(R.id.catalog_search_field)
  val clearSearchButton: ImageButton = view.findViewById(R.id.catalog_clear_search_field_button)
  val movieList: ListView = view.findViewById(R.id.catalog_movie_list)
}