package org.wangyichen.anynote.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import org.wangyichen.anynote.module.AnyNoteApplication.Companion.context
import org.wangyichen.anynote.widget.AlarmReceiver

object ReminderUtils {
  private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

  private fun addRimender(reminder: Long, id: Long, noteId: String) {
    if (reminder > TimeUtils.getTime()) {
      val intent = Intent(context, AlarmReceiver::class.java)
      intent.putExtra(AlarmReceiver.EXTRA_NOTE_ID, noteId)
      val sender = PendingIntent.getBroadcast(
        context, id.toInt(), intent,
        PendingIntent.FLAG_CANCEL_CURRENT
      )
      alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminder, sender)
    }
  }

  fun removeRimender(id: Long) {
    val intent = Intent(context, AlarmReceiver::class.java)
    val sender = PendingIntent.getBroadcast(context, id.toInt(), intent, 0)
    alarmManager.cancel(sender)
    sender.cancel()
  }

  fun updateRimender(reminder: Long, id: Long, noteId: String) {
    removeRimender(id)
    addRimender(reminder, id, noteId)
  }
}