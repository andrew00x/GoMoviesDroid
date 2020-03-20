package com.andrew00x.gomoviesdroid.playlist

import assertk.assertThat
import assertk.assertions.*
import com.activeandroid.query.Select
import com.andrew00x.gomoviesdroid.TestApplication
import com.andrew00x.gomoviesdroid.catalog.Movie
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26], application = TestApplication::class, manifest = Config.NONE)
class PlaylistModelTest {
  private lateinit var underTest: PlaylistModel

  @Before fun setup() {
    underTest = PlaylistModel()
  }

  @Test fun `retrieves list of playlist`() {
    listOf("my playlist 1", "my playlist 2").forEach { Playlist(it).save() }
    assertThat(underTest.getAll()).containsOnly(
        Playlist("my playlist 1"), Playlist("my playlist 2"))
  }

  @Test fun `retrieves items of playlist`() {
    val playlist = Playlist("my playlist 1")
    playlist.save()
    listOf("/movies/gladiator.mkv", "/movies/brave heart.mkv").forEach { PlaylistItem(playlist, it).save() }
    assertThat(underTest.getAll(playlist)).containsOnly(
        PlaylistItem(playlist, "/movies/gladiator.mkv"), PlaylistItem(playlist, "/movies/brave heart.mkv"))
  }

  @Test fun `deletes playlist`() {
    val playlist = Playlist("my playlist 1")
    playlist.save()
    assertThat(Select().from(Playlist::class.java).execute<Playlist>()).contains(playlist)

    val after = underTest.delete(playlist)

    assertThat(after).doesNotContain(playlist)
    assertThat(Select().from(Playlist::class.java).execute<Playlist>()).doesNotContain(playlist)
  }

  @Test fun `deletes playlist item`() {
    val playlist = Playlist("my playlist 1")
    playlist.save()
    val items = listOf("/movies/gladiator.mkv", "/movies/brave heart.mkv").map { PlaylistItem(playlist, it) }
    items.forEach { it.save() }
    assertThat(Select().from(PlaylistItem::class.java).where("playlist = ?", playlist.id).execute<PlaylistItem>())
        .isEqualTo(items)

    val after = underTest.delete(playlist, items[0])

    assertThat(after).containsOnly(items[1])
    assertThat(Select().from(PlaylistItem::class.java).where("playlist = ?", playlist.id).execute<Playlist>())
        .containsOnly(items[1])
  }

  @Test fun `saves playlist`() {
    val playlist = Playlist("my playlist 1")
    assertThat(Select().from(Playlist::class.java).where("name = ?", playlist.name).execute<Playlist>()).isEmpty()
    underTest.save(playlist)
    assertThat(Select().from(Playlist::class.java).where("name = ?", playlist.name).execute<Playlist>())
        .containsOnly(playlist)
  }

  @Test fun `creates playlist with specified name`() {
    val playlist = underTest.create("my playlist one")
    assertThat(Select().from(Playlist::class.java).where("name = ?", playlist.name).execute<Playlist>())
        .containsOnly(playlist)
  }

  @Test fun `creates playlist with generated name`() {
    underTest.create()
    assertThat(Select().from(Playlist::class.java).where("name like ?", "New Playlist %").execute<Playlist>().map { it.name })
        .containsOnly("New Playlist 1")

    underTest.create()
    assertThat(Select().from(Playlist::class.java).where("name like ?", "New Playlist %").execute<Playlist>().map { it.name })
        .containsOnly("New Playlist 1", "New Playlist 2")

    Playlist("New Playlist 10").save()
    underTest.create()
    assertThat(Select().from(Playlist::class.java).where("name like ?", "New Playlist %").execute<Playlist>().map { it.name })
        .containsOnly("New Playlist 1", "New Playlist 2", "New Playlist 10", "New Playlist 11")
  }

  @Test fun `adds item as movie path to playlist`() {
    val playlist = Playlist("my playlist 1")
    playlist.save()
    val item = underTest.addItem(playlist, "/movies/brave heart.mkv")
    assertThat(item.file).isEqualTo("/movies/brave heart.mkv")
    assertThat(Select().from(PlaylistItem::class.java).where("playlist = ?", playlist.id).execute<PlaylistItem>())
        .contains(item)
  }

  @Test fun `adds item as movie to playlist`() {
    val playlist = Playlist("my playlist 1")
    playlist.save()
    val item = underTest.addItem(playlist, Movie(1, "brave heart.mkv", "/movies/brave heart.mkv", "movies", available = true, detailsAvailable = false))
    assertThat(item.file).isEqualTo("/movies/brave heart.mkv")
    assertThat(Select().from(PlaylistItem::class.java).where("playlist = ?", playlist.id).execute<PlaylistItem>())
        .contains(item)
  }
}
