package org.wangyichen.anynote.utils

import org.wangyichen.anynote.module.AnyNoteApplication.Companion.context
import java.io.File

//  启动应用时，缓存大于限制自动清空
class ClearExternalCacheUtils {
  companion object {
    val CLEAR_SIZE = 1024 * 1024 * 100  // 100 M

    fun clear() {
      val dir = context.externalCacheDir
      if (getCacheSize(dir) >= CLEAR_SIZE)
        clear(dir)
    }

    private fun clear(dir: File?) {
      AppExecutors.getInstance().diskIO.execute {
        if (dir != null && dir.isDirectory) {
          val children = dir.list()
          for (i in children.indices) {
            clear(File(dir, children[i]))
          }
          dir.delete()
        } else if (dir != null && dir.isFile) {
          dir.delete()
        }
      }
    }

    private fun getCacheSize(dir: File?): Long {
      if (dir == null) return 0L
      var size: Long = 0
      try {
        val fileList: Array<File> = dir.listFiles()
        for (i in fileList.indices) {
          // 如果下面还有文件
          size = if (fileList[i].isDirectory) {
            size + getCacheSize(fileList[i])
          } else {
            size + fileList[i].length()
          }
        }
      } catch (e: Exception) {
        e.printStackTrace()
      }
      return size
    }
  }
}