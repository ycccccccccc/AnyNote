package org.wangyichen.anynote.source.local.repository

import android.content.Context
import org.wangyichen.anynote.source.Entity.Note
import org.wangyichen.anynote.source.local.NoteDatabase
import org.wangyichen.anynote.source.local.Repository
import org.wangyichen.anynote.utils.AppExecutors
import java.lang.Exception

class NotesRepository private constructor(
  database: NoteDatabase,
  private val executors: AppExecutors
) {
  private val notesDao = database.notesDao()

  fun saveNote(note: Note) {
    executors.diskIO.execute {
      notesDao.insertNote(note)
    }
  }

  fun deleteNoteById(id: String) {
    executors.diskIO.execute {
      notesDao.deleteNoteById(id)
    }
  }

  fun deleteNotesById(ids: List<String>) {
    executors.diskIO.execute {
      notesDao.deleteNotesById(ids)
    }
  }

  fun deleteNotes() {
    executors.diskIO.execute {
      notesDao.deleteNotes(true)
    }
  }

  fun updateNote(note: Note) {
    executors.diskIO.execute {
      notesDao.updateNote(note)
    }
  }

  fun trashNoteById(noteId: String) {
    executors.diskIO.execute {
      notesDao.updateTrashById(true, noteId)
    }
  }
  fun untrashNoteById(noteId: String) {
    executors.diskIO.execute {
      notesDao.updateTrashById(false, noteId)
    }
  }

  fun untrashNotes(noteIds: List<String>) {
    executors.diskIO.execute {
      notesDao.updateTrashs(false, noteIds)
    }
  }

  fun trashNotes(noteIds: List<String>) {
    executors.diskIO.execute {
      notesDao.updateTrashs(true, noteIds)
    }
  }

  fun trashNotesByNotebookId(notebookId: Long) {
    executors.diskIO.execute {
      notesDao.updateTrashByNotebookId(true, notebookId)
    }
  }

  fun updateNotebookId(oldNotebookId: Long, newNotebookId: Long) {
    executors.diskIO.execute {
      notesDao.updateNotebookId(oldNotebookId, newNotebookId)
    }
  }

  fun updatealarmById(alarm: Long, noteId: String) {
    executors.diskIO.execute {
      notesDao.updateAlarm(alarm, noteId)
    }
  }



  fun untoppingNote(note: Note) {
    executors.diskIO.execute {
      notesDao.updateToppingById(false, note.id!!)
    }
  }

  fun changeNoteToppingById(noteId: String, topping: Boolean) {
    executors.diskIO.execute {
      notesDao.updateToppingById(topping, noteId)
    }
  }
  fun toppingNotes(noteIds: List<String>) {
    executors.diskIO.execute {
      notesDao.toppingNotes(true, noteIds)
    }
  }

  fun changeNoteArchiveById(noteId: String, archived: Boolean) {
    executors.diskIO.execute {
      notesDao.updateArchived(archived, noteId)
    }
  }


  fun changeNotesarchive(noteIds: List<String>, archived: Boolean) {
    executors.diskIO.execute {
      notesDao.updateArchiveds(archived, noteIds)
    }
  }


  fun changeNotebookIds( noteIds: List<String>,notebookId: Long) {
    executors.diskIO.execute {
      notesDao.updateBelongNotebookIds(notebookId, noteIds)
    }
  }

  fun getNotes(listener: Repository.LoadListener<List<Note>>) {
    executors.diskIO.execute {
      try {
        val notes = notesDao.getNotes()
        listener.onSuccess(notes)
      } catch (e: Exception) {
        listener.onError(e)
      }
    }
  }

  fun getNoteById(noteid: String) = notesDao.getNoteById(noteid)
  fun getNoteById(noteid: String, listener: Repository.LoadListener<Note>) {
    executors.diskIO.execute {
      try {
        val note = notesDao.getNoLiveNoteById(noteid)
        listener.onSuccess(note)
      } catch (e: Exception) {
        listener.onError(e)
      }
    }
  }

  fun getNotesByNotebookId(notebookId: Long) = notesDao.getNotesByNotebookId(notebookId)

  fun getTrashedNotes() = notesDao.getTrashedNotes(true)

  fun getArchivedNotes() = notesDao.getArchivedNotes(true)

  fun getSketchNotes() = notesDao.getSketchNotes(true)

  companion object {
    private var INSTANCE: NotesRepository? = null
    private val lock = Any()
    fun getInstance(context: Context): NotesRepository {
      synchronized(lock) {
        if (INSTANCE == null) {
          INSTANCE = NotesRepository(NoteDatabase.getInstance(context), AppExecutors.getInstance())
        }
        return INSTANCE!!
      }
    }
  }

}