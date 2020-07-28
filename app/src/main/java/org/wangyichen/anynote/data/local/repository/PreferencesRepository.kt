package org.wangyichen.anynote.data.local.repository

import android.content.Context
import org.wangyichen.anynote.module.AnyNoteApplication
import org.wangyichen.anynote.utils.constant.NotebookIdExt
import org.wangyichen.anynote.utils.constant.SortType

class PreferencesRepository private constructor() {
  private val NOTEBOOKID = "notebookid"
  private val SORT = "sort"
  
  fun saveDefaultNotebookId(notebookId: Long) {
    putLong(NOTEBOOKID, notebookId)
  }

  fun getDefaultNotebookId() = getLong(NOTEBOOKID, NotebookIdExt.ALLNOTES)

  fun saveDefaultSort(sort: Long) {
    putLong(SORT, sort)
  }

  fun getDefaultSort() = getLong(SORT, SortType.LASTMODIFICATION)

  private fun putLong(key: String, value: Long) {
    sharedPrefefrences().edit().apply {
      putLong(key, value)
      apply()
    }
  }

  private fun getLong(key: String, default: Long) = sharedPrefefrences().getLong(key, default)

  private fun sharedPrefefrences() =
    AnyNoteApplication.context.getSharedPreferences("AnyNote", Context.MODE_PRIVATE)

  companion object {
    private var INSTANCE: PreferencesRepository? = null
    private val lock = Any()
    fun getInstance(): PreferencesRepository {
      synchronized(lock) {
        if (INSTANCE == null) {
          INSTANCE =
            PreferencesRepository()
        }
        return INSTANCE!!
      }
    }
  }
}
