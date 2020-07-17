package org.wangyichen.anynote.module

import android.app.Application
import android.content.Context
import android.graphics.Color
import org.wangyichen.anynote.source.Entity.Notebook
import org.wangyichen.anynote.source.local.Repository

const val DEFAULT_NOTEBOOK_ID = 0L

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
    val notebook = Notebook("默认笔记本", Color.TRANSPARENT,"默认笔记本",DEFAULT_NOTEBOOK_ID)
    Repository.getInstance(context).NOTEBOOKS.saveNotebook(notebook)
  }
}