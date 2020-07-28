package org.wangyichen.anynote.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.view.View

//  将view绘制于bitmap
class ViewDrawUtils {
  companion object {
    fun draw(view: View, block: (bitmap: Bitmap) -> Unit) {
      AppExecutors.getInstance().background.execute {
        val canvas = Canvas()
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.RGB_565)
        canvas.setBitmap(bitmap)
        canvas.drawColor(Color.WHITE)
        view.draw(canvas)
        block(bitmap)
      }
    }
  }
}