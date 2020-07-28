package org.wangyichen.anynote.data.local.repository

import android.content.Context
import org.wangyichen.anynote.data.Entity.Note
import org.wangyichen.anynote.data.local.NoteDatabase
import org.wangyichen.anynote.data.local.Repository
import org.wangyichen.anynote.module.DEFAULT_NOTEBOOK_ID
import org.wangyichen.anynote.utils.AppExecutors
import java.lang.Exception

class NotesRepository private constructor(
  database: NoteDatabase,
  private val executors: AppExecutors
) {
  private val notesDao = database.notesDao()
  private var cachedNotes = LinkedHashMap<String, Note>()

  fun saveNote(note: Note) {
    cachedNotes[note.id] = note
    executors.diskIO.execute {
      notesDao.insertNote(note)
    }
  }

  fun deleteNoteById(id: String) {
    cachedNotes[id]?.trashed = true
    executors.diskIO.execute {
      notesDao.deleteNoteById(id)
    }
  }

  fun deleteNotesById(ids: List<String>) {
    ids.forEach { cachedNotes[it]?.trashed = true }
    executors.diskIO.execute {
      notesDao.deleteNotesById(ids)
    }
  }

  fun clearNotes() {
    val iterator = cachedNotes.iterator()
    for (item in iterator) {
      if (item.value.trashed)
        iterator.remove()
    }
    executors.diskIO.execute {
      notesDao.clearNotes(true)
    }
  }

  fun deleteNotebook(notebookId: Long) {
    cachedNotes.values.forEach { if (it.notebookId == notebookId) it.trashed = true }
    executors.diskIO.execute {
      notesDao.updateTrashByNotebookId(true, notebookId)
      notesDao.updateNotebookId(notebookId, DEFAULT_NOTEBOOK_ID)
    }
  }

  fun trashNoteById(noteId: String) {
    cachedNotes[noteId]?.trashed = true
    executors.diskIO.execute {
      notesDao.updateTrashById(true, noteId)
    }
  }

  fun untrashNoteById(noteId: String) {
    cachedNotes[noteId]?.trashed = false
    executors.diskIO.execute {
      notesDao.updateTrashById(false, noteId)
    }
  }

  fun untrashNotes(noteIds: List<String>) {
    noteIds.forEach { cachedNotes[it]?.trashed = false }
    executors.diskIO.execute {
      notesDao.updateTrashs(false, noteIds)
    }
  }

  fun trashNotes(noteIds: List<String>) {
    noteIds.forEach { cachedNotes[it]?.trashed = true }
    executors.diskIO.execute {
      notesDao.updateTrashs(true, noteIds)
    }
  }


  fun updatealarmById(alarm: Long, noteId: String) {
    cachedNotes[noteId]?.alarm = alarm
    executors.diskIO.execute {
      notesDao.updateAlarm(alarm, noteId)
    }
  }


  fun untoppingNote(note: Note) {
    cachedNotes[note.id]?.topping = false
    executors.diskIO.execute {
      notesDao.updateToppingById(false, note.id)
    }
  }

  fun changeNoteToppingById(noteId: String, topping: Boolean) {
    cachedNotes[noteId]?.topping = topping
    executors.diskIO.execute {
      notesDao.updateToppingById(topping, noteId)
    }
  }

  fun toppingNotes(noteIds: List<String>) {
    noteIds.forEach { cachedNotes[it]?.topping = true }
    executors.diskIO.execute {
      notesDao.toppingNotes(true, noteIds)
    }
  }

  fun changeNoteArchiveById(noteId: String, archived: Boolean) {
    cachedNotes[noteId]?.archived = archived
    executors.diskIO.execute {
      notesDao.updateArchived(archived, noteId)
    }
  }

  fun changeNotesarchive(noteIds: List<String>, archived: Boolean) {
    noteIds.forEach { cachedNotes[it]?.archived = archived }
    executors.diskIO.execute {
      notesDao.updateArchiveds(archived, noteIds)
    }
  }

  fun changeNotebookIds(noteIds: List<String>, notebookId: Long) {
    noteIds.forEach { cachedNotes[it]?.notebookId = notebookId }
    executors.diskIO.execute {
      notesDao.updateBelongNotebookIds(notebookId, noteIds)
    }
  }

  fun getNotes(listener: Repository.LoadListener<List<Note>>) {
    executors.diskIO.execute {
      try {
        if (cachedNotes.isEmpty()) {
          val notes = notesDao.getNotes()
          notes.forEach { cachedNotes[it.id] = it }
        }
        listener.onSuccess(cachedNotes.values.toList())
      } catch (e: Exception) {
        listener.onError(e)
      }
    }
  }

  fun getNoteById(noteid: String) = notesDao.getNoteById(noteid)
  fun getNoteById(noteid: String, listener: Repository.LoadListener<Note>) {
    executors.diskIO.execute {
      try {
        if (cachedNotes.isEmpty()) {
          val notes = notesDao.getNotes()
          notes.forEach { cachedNotes[it.id] = it }
        }
        val note = cachedNotes[noteid]?:Note()
        listener.onSuccess(note)
      } catch (e: Exception) {
        listener.onError(e)
      }
    }
  }

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