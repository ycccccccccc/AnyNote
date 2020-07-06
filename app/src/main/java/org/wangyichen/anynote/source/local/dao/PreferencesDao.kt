package org.wangyichen.anynote.source.local.dao

import android.content.Context
import org.wangyichen.anynote.module.AnyNoteApplication
import org.wangyichen.anynote.utils.constant.NotebookIdUtils
import org.wangyichen.anynote.utils.constant.SortUtils

class PreferencesRepository private constructor() {
  private val NOTEBOOKID = "notebookid"
  private val SORT = "sort"

  fun saveDefaultNotebook(notebookId: Long) {
    putLong(NOTEBOOKID, notebookId)
  }

  fun getDefaultNotebook() = getLong(NOTEBOOKID, NotebookIdUtils.ALLNOTES)

  fun saveDefaultSort(sort: Long) {
    putLong(SORT, sort)
  }

  fun getDefaultSort() = getLong(SORT, SortUtils.LASTMODIFICATION)

  private fun putLong(key: String, value: Long) {
    sharedPrefefrences().edit().apply {
      putLong(key, value)
      apply()
    }
  }

  private fun getLong(key: String, default: Long) {
    sharedPrefefrences().getLong(key, default)
  }

  private fun sharedPrefefrences() = AnyNoteApplication.context.getSharedPreferences("AnyNote", Context.MODE_PRIVATE)

  companion object {
    private var INSTANCE: PreferencesRepository? = null
    private val lock = Any()
    fun getInstance(): PreferencesRepository {
      synchronized(lock) {
        if (INSTANCE == null) {
          INSTANCE = PreferencesRepository()
        }
        return INSTANCE !!
      }
    }
  }
}
