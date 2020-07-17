package org.wangyichen.anynote.utils

import android.content.Context
import android.view.WindowManager
import org.wangyichen.anynote.module.AnyNoteApplication.Companion.context


object SystemUtils {

  val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
  val height = wm.defaultDisplay.height
  val width = wm.defaultDisplay.width

  fun getScreenHeight(): Int = height
  fun getScreenWidth(): Int = width

  fun getWidthByPercentage(per: Float) = (getScreenWidth() * per).toInt()
  fun getHeightByPercentage(per: Float) = (getScreenHeight() * per).toInt()

  fun dp2px(dp: Float): Int {
    val scale = context.resources.displayMetrics.density
    return (dp * scale + 0.5f).toInt()
  }
}