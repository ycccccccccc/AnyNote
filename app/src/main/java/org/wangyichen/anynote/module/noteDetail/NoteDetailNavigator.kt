package org.wangyichen.anynote.module.noteDetail

interface NoteDetailNavigator {
  fun editNote(noteId:Long)
  fun ondeletedNote()
}