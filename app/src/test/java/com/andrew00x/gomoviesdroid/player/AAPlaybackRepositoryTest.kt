package com.andrew00x.gomoviesdroid.player

import assertk.assertThat
import assertk.assertions.containsOnly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.activeandroid.query.Select
import com.andrew00x.gomoviesdroid.Playback
import com.andrew00x.gomoviesdroid.TestApplication
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26], application = TestApplication::class, manifest = Config.NONE)
class AAPlaybackRepositoryTest {
  private lateinit var repo: AAPlaybackRepository

  @Before
  fun setup() {
    repo = AAPlaybackRepository()
  }

  @Test
  fun `find playback in db`() {
    val data = AAPlayback("/movies/brave heart.mkv", 300, 1, -1)
    data.save()

    assertThat(repo.find("/movies/brave heart.mkv")).isEqualTo(Playback("/movies/brave heart.mkv", 300, 1, -1))
  }

  @Test
  fun `return null if cannot find playback in db`() {
    assertThat(repo.find("/movies/brave heart.mkv")).isNull()
  }

  @Test
  fun `save playback in db`() {
    assertThat(Select().from(AAPlayback::class.java).execute<AAPlayback>()).isEmpty()

    repo.save(Playback("/movies/brave heart.mkv", 300, 1, -1))

    val data = Select().from(AAPlayback::class.java).execute<AAPlayback>()
    assertThat(data).containsOnly(AAPlayback("/movies/brave heart.mkv", 300, 1, -1))
  }

  @Test
  fun `delete playback from db`() {
    val data = AAPlayback("/movies/brave heart.mkv", 300, 1, -1)
    data.save()

    repo.remove(Playback("/movies/brave heart.mkv", 300, 1, -1))
    assertThat(Select().from(AAPlayback::class.java).execute<AAPlayback>()).isEmpty()
  }

  @Test
  fun `clear playbacks from db`() {
    val data1 = AAPlayback("/movies/brave heart.mkv", 300, 1, -1)
    val data2 = AAPlayback("/movies/lethal weapon.mkv", 400, 1, -1)
    val data3 = AAPlayback("/movies/gladiator.mkv", 500, 1, -1)
    val data4 = AAPlayback("/movies/shawshank redemption.mkv", 700, 1, -1)
    val data5 = AAPlayback("/movies/fight club.mkv", 800, 1, -1)
    data1.save()
    data2.save()
    data3.save()
    data4.save()
    data5.save()

    repo.clear()
    assertThat(Select().from(AAPlayback::class.java).execute<AAPlayback>()).isEmpty()
  }
}