package org.wangyichen.anynote.source.local

import android.content.Context
import org.wangyichen.anynote.source.local.dao.PreferencesRepository
import org.wangyichen.anynote.source.local.repository.AttachmentsRepository
import org.wangyichen.anynote.source.local.repository.NotebooksRepository
import org.wangyichen.anynote.source.local.repository.NotesRepository
import java.lang.Exception

class Repository private constructor(context: Context) {
  val NOTES: NotesRepository by lazy { NotesRepository.getInstance(context) }
  val NOTEBOOKS: NotebooksRepository by lazy { NotebooksRepository.getInstance(context) }
  val ATTACHMENTS: AttachmentsRepository by lazy { AttachmentsRepository.getInstance(context) }
  val PREFERENCES: PreferencesRepository by lazy { PreferencesRepository.getInstance() }

  companion object {
    private var INSTANCE: Repository? = null
    private val lock = Any()
    fun getInstance(context: Context): Repository {
      synchronized(lock) {
        if (INSTANCE == null) {
          INSTANCE = Repository(context)
        }
        return INSTANCE !!
      }
    }
  }

  interface LoadListener {
    fun onSuccess(item: Any)
    fun onError(e: Exception)
  }
}