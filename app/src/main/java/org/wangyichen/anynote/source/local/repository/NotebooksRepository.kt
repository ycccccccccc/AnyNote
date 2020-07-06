package org.wangyichen.anynote.source.local.repository

import android.content.Context
import org.wangyichen.anynote.source.Entity.Notebook
import org.wangyichen.anynote.source.local.NoteDatabase
import org.wangyichen.anynote.utils.AppExecutors

class NotebooksRepository private constructor(
    val database: NoteDatabase,
    val executors: AppExecutors
) {
  private val dao = database.notebooksDao()

  fun saveNotebook(notebook: Notebook) {
    executors.diskIO.execute {
      dao.insertNotebook(notebook)
    }
  }

  fun deleteNotebook(notebook: Notebook) {
    executors.diskIO.execute {
      dao.deleteNotebook(notebook)
    }
  }

  fun updateNotebook(notebook: Notebook) {
    executors.diskIO.execute {
      dao.updateNotebook(notebook)
    }
  }

  fun getNotebookById(id: Long) = dao.getNotebookById(id)

  fun getNotebooks() = dao.getNotebooks()

  companion object {
    private var INSTANCE: NotebooksRepository? = null
    private val lock = Any()
    fun getInstance(context: Context): NotebooksRepository {
      synchronized(lock) {
        if (INSTANCE == null) {
          INSTANCE = NotebooksRepository(NoteDatabase.getInstance(context), AppExecutors.getInstance())
        }
        return INSTANCE !!
      }
    }
  }
}