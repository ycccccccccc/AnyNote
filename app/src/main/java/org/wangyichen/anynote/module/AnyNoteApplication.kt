package org.wangyichen.anynote.module

import android.app.Application
import android.content.Context

class AnyNoteApplication:Application() {
  companion object {
    lateinit var context: Context
  }

  override fun onCreate() {
    super.onCreate()
    context = applicationContext
  }
}