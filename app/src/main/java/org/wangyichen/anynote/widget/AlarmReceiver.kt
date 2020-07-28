package org.wangyichen.anynote.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver : BroadcastReceiver() {
  companion object {
    const val EXTRA_NOTE_ID = "EXTRA_NOTE_ID"
  }

  override fun onReceive(context: Context, intent: Intent) {
    NotificationUtils.openNoteNotification(intent.getStringExtra(EXTRA_NOTE_ID) ?: "")
  }
}