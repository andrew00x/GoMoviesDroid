package com.andrew00x.gomoviesdroid

class GomoviesApplication : com.activeandroid.app.Application() {
  private val lock = this

  private var _component: MainGomoviesComponent? = null
  val component: MainGomoviesComponent
    get() {
      synchronized(lock) {
        return _component!!
      }
    }

  override fun onCreate() {
    super.onCreate()
    createComponent()
  }

  fun refreshComponent() {
    synchronized(lock) {
      createComponent()
    }
  }

  private fun createComponent() {
    synchronized(lock) {
      _component = DaggerMainGomoviesComponent.builder().mainGomoviesModule(MainGomoviesModule(this)).build()
    }
  }
}