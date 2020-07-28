package org.wangyichen.anynote.data.local

import android.content.Context
import org.wangyichen.anynote.data.local.repository.PreferencesRepository
import org.wangyichen.anynote.data.local.repository.CoverRepository
import org.wangyichen.anynote.data.local.repository.NotebooksRepository
import org.wangyichen.anynote.data.local.repository.NotesRepository
import java.lang.Exception

class Repository private constructor(context: Context) {
  val NOTES: NotesRepository by lazy { NotesRepository.getInstance(context) }
  val NOTEBOOKS: NotebooksRepository by lazy { NotebooksRepository.getInstance(context) }
  val PREFERENCES: PreferencesRepository by lazy { PreferencesRepository.getInstance() }
  val COVER: CoverRepository by lazy { CoverRepository.getInstance() }

  companion object {
    private var INSTANCE: Repository? = null
    private val lock = Any()
    fun getInstance(context: Context): Repository {
      synchronized(lock) {
        if (INSTANCE == null) {
          INSTANCE = Repository(context)
        }
        return INSTANCE!!
      }
    }
  }

  interface LoadListener<T> {
    fun onSuccess(item: T)
    fun onError(e: Exception)
  }
}