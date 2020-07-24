package org.wangyichen.anynote.module.noteDetail

interface NoteDetailNavigator {
  fun editNote(noteId: String)
  fun ondeletedNote()
}