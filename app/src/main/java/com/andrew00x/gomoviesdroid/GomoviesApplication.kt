package com.andrew00x.gomoviesdroid

class GomoviesApplication : com.activeandroid.app.Application() {
    val component: GomoviesComponent by lazy {
        DaggerGomoviesComponent.builder().gomoviesModule(GomoviesModule()).build()
    }
}