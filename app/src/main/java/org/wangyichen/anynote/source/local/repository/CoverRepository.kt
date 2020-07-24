package org.wangyichen.anynote.source.local.repository

import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.FileUtils
import android.provider.MediaStore
import android.util.Log
import androidx.core.net.toFile
import org.wangyichen.anynote.module.AnyNoteApplication.Companion.context
import org.wangyichen.anynote.utils.AppExecutors
import org.wangyichen.anynote.utils.TimeUtils
import java.io.*

class CoverRepository {
  val IMAGES_FOLDER_NAME = "AnyNote"
  val executors = AppExecutors.getInstance()

  //  保存图片到本地
  fun saveImageToDCIM(bitmap: Bitmap, name: String) {
    var fos: OutputStream? = null
    val finalName = "${name}_${TimeUtils.getTime()}"

    executors.diskIO.execute {
      try {
        fos = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
          val resolver: ContentResolver = context.getContentResolver()
          val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, finalName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/$IMAGES_FOLDER_NAME")
          }
          val imageUri =
            resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
          resolver.openOutputStream(imageUri!!)
        } else {
          val imagesDir = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DCIM
          ).toString() + File.separator + IMAGES_FOLDER_NAME
          val file = File(imagesDir)
          if (!file.exists()) {
            file.mkdir()
          }
          val image = File(imagesDir, finalName + ".png")
          FileOutputStream(image)
        }
        val saved = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos!!.flush()
      } catch (e: Exception) {
        Log.d(this.javaClass.name, e.toString())

      } finally {
        if (fos != null)
          fos!!.close()
      }
    }
  }

  fun saveImageToCache(bitmap: Bitmap): Uri {
    val imagesDir = context.externalCacheDir
    val finalName = TimeUtils.getTime().toString()
    val image = File(imagesDir, finalName + ".png")
    AppExecutors.getInstance().diskIO.execute {
      FileOutputStream(image).use { fos ->
        try {
          val saved = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
          fos.flush()
        } catch (e: java.lang.Exception) {
          Log.d(this.javaClass.name, e.toString())
        }
      }
    }

    return Uri.parse(image.absolutePath)
  }

  //  保存图片应用空间
  fun saveImageToExternal(uri: Uri): Uri {
    val imagesDir = context.getExternalFilesDir(null)
    val finalName = TimeUtils.getTime().toString()
    val image = File(imagesDir, finalName + ".png")
    AppExecutors.getInstance().diskIO.execute {
      var contentResolverInputStream: InputStream? = null
      var contentResolverOutputStream: OutputStream? = null
      try {
        contentResolverInputStream = context.contentResolver.openInputStream(uri)!!
        contentResolverOutputStream = FileOutputStream(image)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
          FileUtils.copy(contentResolverInputStream, contentResolverOutputStream)
        } else {
//          TODO 其他版本
        }
      } catch (e: IOException) {
        Log.e(this.javaClass.name, e.toString())
      }
    }
    return Uri.fromFile(image)
  }

  //  删除图片
  fun deleteImageIfExist(uri: Uri) {
    if (uri == Uri.EMPTY) return
    AppExecutors.getInstance().diskIO.execute {
      val image = uri.toFile()
      try {
        image.delete()
      } catch (e: IOException) {
        Log.e(this.javaClass.name, e.toString())
      }
    }
  }

  //  批量删除图片
  fun deleteImages(uris: List<Uri>) {
    for (uri in uris) {
      deleteImageIfExist(uri)
    }
  }

  companion object{
    private var INSTANCE: CoverRepository? = null
    private val lock = Any()
    fun getInstance(): CoverRepository {
      synchronized(lock) {
        if (INSTANCE == null) {
          INSTANCE = CoverRepository()
        }
        return INSTANCE!!
      }
    }
  }
}