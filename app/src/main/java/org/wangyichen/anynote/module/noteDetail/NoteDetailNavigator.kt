package org.wangyichen.anynote.module.noteDetail

import android.net.Uri

interface NoteDetailNavigator {
  fun editNote(noteId: String)
  fun onDeletedNote()
  fun showCover(uri: Uri)
}