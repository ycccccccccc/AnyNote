package org.wangyichen.anynote.module

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import org.wangyichen.anynote.R
import org.wangyichen.anynote.data.Entity.Notebook
import org.wangyichen.anynote.data.local.Repository
import org.wangyichen.anynote.utils.ClearExternalCacheUtils
import org.wangyichen.anynote.widget.NotificationUtils.CHANNEL_ID

const val DEFAULT_NOTEBOOK_ID = 0L

class AnyNoteApplication : Application() {
  companion object {
    lateinit var context: Context
  }

  override fun onCreate() {
    super.onCreate()
    context = applicationContext
    initDatabase()
    clearCache()
    createNotificationChannel()
  }

  private fun initDatabase() {
    val notebook = Notebook("默认笔记本", resources.getColor(R.color.colorPrimary), "默认笔记本", DEFAULT_NOTEBOOK_ID)
    Repository.getInstance(context).NOTEBOOKS.saveNotebook(notebook)
  }

  private fun clearCache() {
    ClearExternalCacheUtils.clear()
  }

  private fun createNotificationChannel() {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val name = getString(R.string.channel_name)
      val descriptionText = getString(R.string.channel_description)
      val importance = NotificationManager.IMPORTANCE_DEFAULT
      val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
        description = descriptionText
      }
      // Register the channel with the system
      val notificationManager: NotificationManager =
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      notificationManager.createNotificationChannel(channel)
    }
  }
}