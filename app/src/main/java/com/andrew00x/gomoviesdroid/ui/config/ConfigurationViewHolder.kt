package com.andrew00x.gomoviesdroid.ui.config

import android.view.View
import android.widget.Button
import android.widget.EditText
import com.andrew00x.gomoviesdroid.R

class ConfigurationViewHolder(view: View) {
  val server: EditText = view.findViewById(R.id.configuration_server_edit)
  val port: EditText = view.findViewById(R.id.configuration_port_edit)
  val detailLangs: EditText = view.findViewById(R.id.configuration_detail_langs_edit)
  val save: Button = view.findViewById(R.id.configuration_save_configuration_button)
}
