package org.wangyichen.anynote.source.local.dao

import android.content.Context
import org.wangyichen.anynote.module.AnyNoteApplication
import org.wangyichen.anynote.utils.constant.FilterType
import org.wangyichen.anynote.utils.constant.NotebookIdExt
import org.wangyichen.anynote.utils.constant.SortType

class PreferencesRepository private constructor() {
  private val NOTEBOOKID = "notebookid"
  private val SORT = "sort"
  private val FILTER = "filter"
  private val NOTEBOOKNAME = "notebookname"

  fun saveDefaultNotebookId(notebookId: Long) {
    putLong(NOTEBOOKID, notebookId)
  }

  fun getDefaultNotebookId() = getLong(NOTEBOOKID, NotebookIdExt.ALLNOTES)

  fun saveDefaultSort(sort: Long) {
    putLong(SORT, sort)
  }

  fun getDefaultSort() = getLong(SORT, SortType.LASTMODIFICATION)

  fun saveDeafultFilter(filter: Long) {
    putLong(FILTER, filter)
  }

  fun getDefaultFilter() = getLong(FILTER, FilterType.HAS_IMAGE)
  fun saveDeafultNotebookName(name: String) {
    putString(NOTEBOOKNAME, name)
  }

  fun getDefaultNotebookName() = getString(NOTEBOOKNAME, "全部笔记")

  private fun putLong(key: String, value: Long) {
    sharedPrefefrences().edit().apply {
      putLong(key, value)
      apply()
    }
  }

  private fun getLong(key: String, default: Long) = sharedPrefefrences().getLong(key, default)

  private fun putString(key: String, value: String) {
    sharedPrefefrences().edit().apply {
      putString(key, value)
      apply()
    }
  }

  private fun getString(key: String, default: String) = sharedPrefefrences().getString(key, default)

  private fun sharedPrefefrences() =
    AnyNoteApplication.context.getSharedPreferences("AnyNote", Context.MODE_PRIVATE)

  companion object {
    private var INSTANCE: PreferencesRepository? = null
    private val lock = Any()
    fun getInstance(): PreferencesRepository {
      synchronized(lock) {
        if (INSTANCE == null) {
          INSTANCE = PreferencesRepository()
        }
        return INSTANCE!!
      }
    }
  }
}
