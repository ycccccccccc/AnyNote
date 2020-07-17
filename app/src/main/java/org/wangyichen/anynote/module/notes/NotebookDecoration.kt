package org.wangyichen.anynote.module.notes

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import org.wangyichen.anynote.R
import org.wangyichen.anynote.module.AnyNoteApplication.Companion.context


class NotebookDecoration : RecyclerView.ItemDecoration() {
  val divider = context.resources.getDrawable(R.drawable.divider_notebook)
  val divider_normal = context.resources.getDrawable(R.drawable.divider_notebook_normal)
  val position = 3 // 特殊间隔


  override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
    super.onDraw(c, parent, state)

    var left = 0
    var right = parent.width

    for (i in 0 until parent.childCount) {
      val child = parent.getChildAt(i)

      val mBounds = Rect()
      parent.getDecoratedBoundsWithMargins(child, mBounds)

      if (i == position) {
        var bottom: Int = mBounds.bottom + Math.round(child.getTranslationY())
        var top: Int = bottom - divider.getIntrinsicHeight()
        divider.setBounds(left, top, right, bottom)
        divider.draw(c)
      } else {
        var bottom: Int = mBounds.bottom + Math.round(child.getTranslationY())
        var top: Int = bottom - divider_normal.getIntrinsicHeight()
        divider_normal.setBounds(left, top, right, bottom)
        divider_normal.draw(c)
      }
    }
  }

  override fun getItemOffsets(
    outRect: Rect,
    view: View,
    parent: RecyclerView,
    state: RecyclerView.State
  ) {
    if (parent.getChildLayoutPosition(view) == position) {
      outRect.set(0, 0, 0, divider.getIntrinsicHeight())
    } else {
      outRect.set(0, 0, 0, divider_normal.getIntrinsicHeight())
    }
  }
}