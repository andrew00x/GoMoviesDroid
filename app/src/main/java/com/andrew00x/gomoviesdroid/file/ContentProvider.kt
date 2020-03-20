package com.andrew00x.gomoviesdroid.file

import android.content.ContentResolver
import android.net.Uri
import java.io.FileNotFoundException
import java.io.InputStream
import javax.inject.Inject

interface ContentProvider {
  fun getContent(uri: Uri): InputStream

  fun getBytes(uri: Uri): ByteArray {
    return getContent(uri).use { it.readBytes() }
  }
}

class DefaultContentProvider @Inject constructor(private val contentResolver: ContentResolver) : ContentProvider {
  override fun getContent(uri: Uri): InputStream {
    return contentResolver.openInputStream(uri) ?: throw FileNotFoundException(uri.toString())
  }
}