package org.wangyichen.anynote.source.local.repository

import android.content.Context
import org.wangyichen.anynote.module.DEFAULT_NOTEBOOK_ID
import org.wangyichen.anynote.source.Entity.Notebook
import org.wangyichen.anynote.source.local.NoteDatabase
import org.wangyichen.anynote.source.local.Repository
import org.wangyichen.anynote.utils.AppExecutors
import java.lang.Exception

class NotebooksRepository private constructor(
    val database: NoteDatabase,
    val executors: AppExecutors
) {
  private val notebooksDao = database.notebooksDao()
  private val notesDao = database.notesDao()

  fun saveNotebook(notebook: Notebook) {
    executors.diskIO.execute {
      notebooksDao.insertNotebook(notebook)
    }
  }

  fun deleteNotebook(notebookId: Long) {
    executors.diskIO.execute {
      notesDao.updateTrashByNotebookId(true,notebookId)
      notesDao.updateNotebookId(notebookId, DEFAULT_NOTEBOOK_ID)
      notebooksDao.deleteNotebookById(notebookId)
    }
  }

  fun updateNotebook(notebook: Notebook) {
    executors.diskIO.execute {
      notebooksDao.updateNotebook(notebook)
    }
  }

  fun getNotebookById(id: Long) = notebooksDao.getNotebookById(id)

  fun getNoLiveNotebookById(notebookid: Long, listener: Repository.LoadListener<Notebook>) {
    executors.diskIO.execute {
      try {
        val notebook = notebooksDao.getNoLiveNotebookById(notebookid)
        listener.onSuccess(notebook)
      } catch (e: Exception) {
        listener.onError(e)
      }
    }
  }


  fun getNotebooks() = notebooksDao.getNotebooks()

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