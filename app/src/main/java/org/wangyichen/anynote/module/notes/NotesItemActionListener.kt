package org.wangyichen.anynote.module.notes

import org.wangyichen.anynote.source.Entity.Note

interface NotesItemActionListener {
  fun onNoteClicked(note:Note)
  fun onToppingClicked(note: Note):Boolean
}