package org.wangyichen.anynote.module.notes

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import org.wangyichen.anynote.R
import org.wangyichen.anynote.module.AnyNoteApplication.Companion.context
import kotlin.math.roundToInt


class NotebookDecoration : RecyclerView.ItemDecoration() {
  private val divider = context.resources.getDrawable(R.drawable.divider_notebook)
  private val dividerNormal = context.resources.getDrawable(R.drawable.divider_notebook_normal)
  private val position = 3 // 特殊间隔

  override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
    super.onDraw(c, parent, state)

    val left = 0
    val right = parent.width
    val count = parent.childCount

    for (i in 0 until count) {
      c.save()
      val child = parent.getChildAt(i)

      val mBounds = Rect()
      parent.getDecoratedBoundsWithMargins(child, mBounds)

//      第position 个需要绘制不同的边界
      if (parent.getChildAdapterPosition(child) == position) {
        val bottom: Int = mBounds.bottom + child.translationY.roundToInt()
        val top: Int = bottom - divider.intrinsicHeight
        divider.setBounds(left, top, right, bottom)
        divider.draw(c)
      } else {
        val bottom: Int = mBounds.bottom + child.translationY.roundToInt()
        val top: Int = bottom - dividerNormal.intrinsicHeight
        dividerNormal.setBounds(left, top, right, bottom)
        dividerNormal.draw(c)
      }
      c.restore()
    }
  }

  override fun getItemOffsets(
    outRect: Rect,
    view: View,
    parent: RecyclerView,
    state: RecyclerView.State
  ) {
    if (parent.getChildLayoutPosition(view) == position) {
      outRect.set(0, 0, 0, divider.intrinsicHeight)
    } else {
      outRect.set(0, 0, 0, dividerNormal.intrinsicHeight)
    }
  }
}