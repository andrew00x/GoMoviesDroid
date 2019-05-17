package com.andrew00x.gomoviesdroid

import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.internal.schedulers.ImmediateThinScheduler
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.TestScheduler
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class SchedulersSetup : TestRule {
  val mainScheduler = ImmediateThinScheduler.INSTANCE!!
  val ioScheduler = ImmediateThinScheduler.INSTANCE!!
  val computationScheduler = TestScheduler()

  override fun apply(base: Statement?, descr: Description?): Statement {
    return object : Statement() {
      override fun evaluate() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { mainScheduler }
        RxJavaPlugins.setInitIoSchedulerHandler { ioScheduler }
        RxJavaPlugins.setComputationSchedulerHandler { computationScheduler }
        try {
          base!!.evaluate()
        } finally {
          RxAndroidPlugins.reset()
          RxJavaPlugins.reset()
        }
      }
    }
  }
}