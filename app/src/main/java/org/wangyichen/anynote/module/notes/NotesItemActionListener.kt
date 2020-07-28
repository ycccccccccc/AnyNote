package org.wangyichen.anynote.module.notes

import org.wangyichen.anynote.data.Entity.Note

//  note项的点击事件
interface NotesItemActionListener {
  fun onNoteClicked(note:Note)
  fun onToppingClicked(note: Note)
}