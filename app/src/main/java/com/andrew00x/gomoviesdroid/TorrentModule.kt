package com.andrew00x.gomoviesdroid

import com.andrew00x.gomoviesdroid.file.ContentProvider
import com.andrew00x.gomoviesdroid.torrent.TorrentModel
import com.andrew00x.gomoviesdroid.torrent.TorrentPresenter
import dagger.Module
import dagger.Provides

@TorrentScope
@Module
class TorrentModule {
  @TorrentScope
  @Provides
  fun torrentPresenter(model: TorrentModel, errorHandler: ErrorHandler, torrentContentProvider: ContentProvider): TorrentPresenter =
      TorrentPresenter(model, errorHandler, torrentContentProvider)
}
