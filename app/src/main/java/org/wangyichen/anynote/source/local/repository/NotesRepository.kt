package org.wangyichen.anynote.source.local.repository

import android.content.Context
import androidx.lifecycle.LiveData
import org.wangyichen.anynote.source.Entity.Note
import org.wangyichen.anynote.source.local.NoteDatabase
import org.wangyichen.anynote.utils.AppExecutors

class NotesRepository private constructor(
  val database: NoteDatabase,
  val executors: AppExecutors
) {
  private val dao = database.notesDao()

  fun saveNote(note: Note) {
    executors.diskIO.execute {
      dao.insertNote(note)
    }
  }

  fun deleteNotesById(ids: List<Long>) {
    executors.diskIO.execute {
      dao.deleteNotesById(ids)
    }
  }

  fun updateNote(note: Note) {
    executors.diskIO.execute {
      dao.updateNote(note)
    }
  }

  fun trashNote(note: Note) {
    executors.diskIO.execute {
      dao.updateTrash(true, note.id!!)
    }
  }

  fun untrashNote(note: Note) {
    executors.diskIO.execute {
      dao.updateTrash(false, note.id!!)
    }
  }

  fun trashNotes(notes: List<Note>) {
    executors.diskIO.execute {
      dao.updateTrashs(true, notes.map { it.id!! })
    }
  }

  fun untrashNotes(notes: List<Note>) {
    executors.diskIO.execute {
      dao.updateTrashs(false, notes.map { it.id!! })
    }
  }

  fun toppingNote(note: Note) {
    executors.diskIO.execute {
      dao.updateTopping(true, note.id!!)
    }
  }

  fun untoppingNote(note: Note) {
    executors.diskIO.execute {
      dao.updateTopping(false, note.id!!)
    }
  }

  fun archiveNote(note: Note) {
    executors.diskIO.execute {
      dao.updateArchived(true, note.id!!)
    }
  }

  fun unarchiveNote(note: Note) {
    executors.diskIO.execute {
      dao.updateArchived(false, note.id!!)
    }
  }

  fun archiveNotes(notes: List<Note>) {
    executors.diskIO.execute {
      dao.updateArchiveds(true, notes.map { it.id!! })
    }
  }

  fun unarchiveNotes(notes: List<Note>) {
    executors.diskIO.execute {
      dao.updateArchiveds(false, notes.map { it.id!! })
    }
  }

  fun changeNotebookIds(notebookId: Long, notes: List<Note>) {
    executors.diskIO.execute {
      dao.updateBelongNotebookIds(notebookId, notes.map { it.id!! })
    }
  }

  fun getNotes() = dao.getNotes()

  fun getNoteById(noteid: Long) = dao.getNoteById(noteid)

  fun getNotesByNotebookId(notebookId: Long) = dao.getNotesByNotebookId(notebookId)

  fun getTrashedNotes() = dao.getTrashedNotes(true)

  fun getArchivedNotes() = dao.getArchivedNotes(true)

  fun getSketchNotes() = dao.getSketchNotes(true)

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