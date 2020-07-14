package org.wangyichen.anynote.module

import android.app.Application
import android.content.Context
import android.graphics.Color
import org.wangyichen.anynote.source.Entity.Notebook
import org.wangyichen.anynote.source.local.Repository

class AnyNoteApplication:Application() {
  companion object {
    lateinit var context: Context
  }

  override fun onCreate() {
    super.onCreate()
    context = applicationContext
    initDatabase()
  }
  private fun initDatabase() {
    val notebook = Notebook("默认", Color.TRANSPARENT,"默认笔记本",0)
    Repository.getInstance(context).NOTEBOOKS.saveNotebook(notebook)
  }
}