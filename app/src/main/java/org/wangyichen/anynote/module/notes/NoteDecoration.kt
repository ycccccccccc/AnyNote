package org.wangyichen.anynote.module.notes

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import org.wangyichen.anynote.R
import org.wangyichen.anynote.module.AnyNoteApplication.Companion.context

class NoteDecoration : RecyclerView.ItemDecoration() {
  val divider = context.resources.getDrawable(R.drawable.divider_notebook)

  override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
    val notes = (parent.adapter as NotesAdapter).notes
    var left = 0
    var right = parent.width

    for (idx in 0 until parent.childCount) {
      if (idx != parent.childCount - 1) {
        val nowNote = notes[idx]
        val nextNote = notes[idx + 1]

        val child = parent.getChildAt(idx)

        if (nowNote.note.topping && !nextNote.note.topping) {
          val mBounds = Rect()
          parent.getDecoratedBoundsWithMargins(child, mBounds)
          var bottom: Int = mBounds.bottom + Math.round(child.getTranslationY())
          var top: Int = bottom - divider.getIntrinsicHeight()
          divider.setBounds(left, top, right, bottom)
          divider.draw(c)
        }
      }
    }
  }

  override fun getItemOffsets(
    outRect: Rect,
    view: View,
    parent: RecyclerView,
    state: RecyclerView.State
  ) {
    val idx = parent.getChildLayoutPosition(view)

    val notes = (parent.adapter as NotesAdapter).notes
    if (idx != notes.size - 1) {
      val nowNote = notes[idx]
      val nextNote = notes[idx + 1]

      if (nowNote.note.topping && !nextNote.note.topping) {
        outRect.bottom = divider.intrinsicHeight
      }
    }
  }
}