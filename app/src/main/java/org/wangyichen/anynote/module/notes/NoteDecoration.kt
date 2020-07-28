package org.wangyichen.anynote.module.notes

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import org.wangyichen.anynote.R
import org.wangyichen.anynote.module.AnyNoteApplication.Companion.context
import kotlin.math.roundToInt

class NoteDecoration : RecyclerView.ItemDecoration() {
  private val divider = context.resources.getDrawable(R.drawable.divider_notebook)

  override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
    val left = 0
    val right = parent.width

    for (idx in 0 until parent.childCount) {
      val child = parent.getChildAt(idx)
      val position = parent.getChildAdapterPosition(child)
      if (hasDivider(position, parent)) {
        val mBounds = Rect()
        parent.getDecoratedBoundsWithMargins(child, mBounds)
        val bottom: Int = mBounds.bottom + child.translationY.roundToInt()
        val top: Int = bottom - divider.intrinsicHeight
        divider.setBounds(left, top, right, bottom)
        divider.draw(c)
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

    if (hasDivider(idx, parent)) {
      outRect.bottom = divider.intrinsicHeight
    }
  }

//  判断当前是否是置顶分界线
  private fun hasDivider(position: Int, parent: RecyclerView): Boolean {
    val notes = (parent.adapter as NotesAdapter).notes
    if (position != notes.size - 1) {
      val nowNote = notes[position].note
      val nextNote = notes[position + 1].note

      if (nowNote.topping && !nextNote.topping) {
        return true
      }
    }
    return false
  }
}