package com.andrew00x.gomoviesdroid

class GomoviesApplication : com.activeandroid.app.Application() {
  private val lock = this

  private var _component: GomoviesComponent? = null
  val component: GomoviesComponent
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
      _component = DaggerGomoviesComponent.builder().gomoviesModule(GomoviesModule(this)).build()
    }
  }
}