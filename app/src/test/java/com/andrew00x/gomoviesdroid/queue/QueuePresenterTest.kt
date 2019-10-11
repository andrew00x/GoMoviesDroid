package com.andrew00x.gomoviesdroid.queue

import com.andrew00x.gomoviesdroid.ErrorHandler
import com.andrew00x.gomoviesdroid.Event
import com.andrew00x.gomoviesdroid.SchedulersSetup
import io.reactivex.Single
import org.junit.After
import org.junit.Before
import org.junit.Rule

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.Mockito.`when` as whenInvoke

@RunWith(MockitoJUnitRunner::class)
class QueuePresenterTest {
  @Rule
  @JvmField
  val schedulers = SchedulersSetup()

  @Mock private lateinit var model: QueueModel
  @Mock private lateinit var errorHandler: ErrorHandler
  @Mock private lateinit var view: QueueView
  @InjectMocks private lateinit var underTest: QueuePresenter

  private lateinit var clickQueueItem: Event<QueueItem>
  private lateinit var clickDeleteQueueItem: Event<QueueItem>
  private lateinit var clickRefresh: Event<Any>

  @Before fun setup() {
    clickQueueItem = Event()
    clickDeleteQueueItem = Event()
    clickRefresh = Event()
    whenInvoke(view.clickOnItem()).thenReturn(clickQueueItem)
    whenInvoke(view.clickOnDelete()).thenReturn(clickDeleteQueueItem)
    whenInvoke(view.clickOnRefresh()).thenReturn(clickRefresh)
  }

  @After fun cleanup() {
    underTest.detach()
  }

  @Test fun `refreshes on attach`() {
    val queue = listOf(
        QueueItem("/movies/gladiator.mkv", 0),
        QueueItem("/movies/brave heart.mkv", 1)
    )
    whenInvoke(model.getAll()).thenReturn(Single.just(queue))

    underTest.attach(view)

    verifyViewUpdated(queue)
  }

  @Test fun `notifies ErrorHandler when error occurred while loading data`() {
    val err = RuntimeException("failed")
    whenInvoke(model.getAll()).thenReturn(Single.error(err))

    underTest.attach(view)

    verifyErrorHandled(err)
  }

  @Test fun `removes queue item when click on delete`() {
    val queue = listOf(
        QueueItem("/movies/gladiator.mkv", 0),
        QueueItem("/movies/brave heart.mkv", 1)
    )
    whenInvoke(model.getAll()).thenReturn(Single.just(queue))
    underTest.attach(view)

    val toRemove = queue[0]
    val afterRemove = listOf(QueueItem("/movies/brave heart.mkv", 0))
    whenInvoke(model.remove(toRemove)).thenReturn(Single.just(afterRemove))

    clickDeleteQueueItem.send(toRemove)

    verify(model).remove(toRemove)
    verifyViewUpdated(afterRemove)
  }

  @Test fun `shifts in queue when item clicked`() {
    val queue = listOf(
        QueueItem("/movies/gladiator.mkv", 0),
        QueueItem("/movies/brave heart.mkv", 1),
        QueueItem("/movies/shawshank redemption.mkv", 2)
    )
    whenInvoke(model.getAll()).thenReturn(Single.just(queue))
    underTest.attach(view)

    val shiftTo = queue[0]
    val afterShift = listOf(QueueItem("/movies/brave heart.mkv", 0), QueueItem("/movies/shawshank redemption.mkv", 1))
    whenInvoke(model.shift(shiftTo)).thenReturn(Single.just(afterShift))

    clickQueueItem.send(shiftTo)

    verify(model).shift(shiftTo)
    verifyViewUpdated(afterShift)
  }

  @Test fun `notifies ErrorHandler when error occurred while delete`() {
    val queue = listOf(
        QueueItem("/movies/gladiator.mkv", 0),
        QueueItem("/movies/brave heart.mkv", 1)
    )
    whenInvoke(model.getAll()).thenReturn(Single.just(queue))
    underTest.attach(view)

    val toRemove = queue[0]
    val err = RuntimeException("failed")
    whenInvoke(model.remove(toRemove)).thenReturn(Single.error(err))

    clickDeleteQueueItem.send(toRemove)

    verifyErrorHandled(err)
  }

  @Test fun `refreshes queue when refresh button clicked`() {
    whenInvoke(model.getAll()).thenReturn(Single.just(listOf()))
    underTest.attach(view)
    val queue = listOf(
        QueueItem("/movies/gladiator.mkv", 0),
        QueueItem("/movies/brave heart.mkv", 1)
    )
    whenInvoke(model.getAll()).thenReturn(Single.just(queue))

    clickRefresh.send(Unit)

    verify(view).setQueue(queue)
  }

  private fun verifyViewUpdated(queue: List<QueueItem>) {
    val inOrder = Mockito.inOrder(view)
    inOrder.verify(view).showLoader()
    inOrder.verify(view).hideLoader()
    inOrder.verify(view).setQueue(queue)
  }

  private fun verifyErrorHandled(err: Throwable) {
    val inOrder = Mockito.inOrder(view, errorHandler)
    inOrder.verify(view).showLoader()
    inOrder.verify(view).hideLoader()
    inOrder.verify(errorHandler).handleError(view, err)
  }
}
