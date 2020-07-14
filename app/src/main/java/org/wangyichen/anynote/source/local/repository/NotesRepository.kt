package org.wangyichen.anynote.source.local.repository

import android.content.Context
import org.wangyichen.anynote.source.Entity.Note
import org.wangyichen.anynote.source.Entity.NoteWithOthers
import org.wangyichen.anynote.source.local.NoteDatabase
import org.wangyichen.anynote.utils.AppExecutors
import java.lang.Exception

class NotesRepository private constructor(
  private val database: NoteDatabase,
  private val executors: AppExecutors
) {
  private val notesDao = database.notesDao()

  fun saveNote(note: Note) {
    executors.diskIO.execute {
      notesDao.insertNote(note)
    }
  }

  fun deleteNoteById(id: Long) {
    executors.diskIO.execute {
      notesDao.deleteNoteById(id)
    }
  }

  fun deleteNotesById(ids: List<Long>) {
    executors.diskIO.execute {
      notesDao.deleteNotesById(ids)
    }
  }

  fun updateNote(note: Note) {
    executors.diskIO.execute {
      notesDao.updateNote(note)
    }
  }

  fun trashNoteById(noteId: Long) {
    executors.diskIO.execute {
      notesDao.updateTrashById(true, noteId)
    }
  }

  fun untrashNote(note: Note) {
    executors.diskIO.execute {
      notesDao.updateTrashById(false, note.id!!)
    }
  }

  fun trashNotes(notes: List<Note>) {
    executors.diskIO.execute {
      notesDao.updateTrashs(true, notes.map { it.id!! })
    }
  }

  fun untrashNotes(notes: List<Note>) {
    executors.diskIO.execute {
      notesDao.updateTrashs(false, notes.map { it.id!! })
    }
  }


  fun untoppingNote(note: Note) {
    executors.diskIO.execute {
      notesDao.updateTopping(false, note.id!!)
    }
  }

  fun changeNoteToppingById(noteId: Long, topping: Boolean) {
    executors.diskIO.execute {
      notesDao.updateTopping(topping, noteId)
    }
  }

  fun changeNoteArchiveById(noteId: Long, archived: Boolean) {
    executors.diskIO.execute {
      notesDao.updateArchived(archived, noteId)
    }
  }


  fun changeNotesarchive(notes: List<Note>, archived: Boolean) {
    executors.diskIO.execute {
      notesDao.updateArchiveds(archived, notes.map { it.id!! })
    }
  }


  fun changeNotebookIds(notebookId: Long, notes: List<Note>) {
    executors.diskIO.execute {
      notesDao.updateBelongNotebookIds(notebookId, notes.map { it.id!! })
    }
  }

  fun getNotes(listener: LoadListener) {
    executors.diskIO.execute {
      try {
        val notes = notesDao.getNotes()
        listener.onSuccess(notes)
      } catch (e: Exception) {
        listener.onError(e)
      }
    }
  }

  fun getNoteById(noteid: Long) = notesDao.getNoteById(noteid)
  fun getNoteById(noteid: Long, listener: LoadListener) {
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

  interface LoadListener {
    fun onSuccess(item: Any)
    fun onError(e: Exception)
  }
}