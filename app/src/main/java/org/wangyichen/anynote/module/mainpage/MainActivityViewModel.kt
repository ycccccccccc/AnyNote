package org.wangyichen.anynote.module.mainpage

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import org.wangyichen.anynote.module.AnyNoteApplication
import org.wangyichen.anynote.source.Entity.Note
import org.wangyichen.anynote.source.Entity.NoteWithOthers
import org.wangyichen.anynote.source.Entity.Notebook
import org.wangyichen.anynote.source.local.Repository

class MainActivityViewModel : ViewModel() {
  var notes : LiveData<List<NoteWithOthers>>? = null
  val repository = Repository.getInstance(AnyNoteApplication.context)
  fun getNotes() {
    repository.NOTEBOOKS.saveNotebook(Notebook())
    notes = repository.NOTES.getNotes()
  }
  fun addNote() {
    repository.NOTES.saveNote(Note())
  }
}