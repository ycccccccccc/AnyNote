package org.wangyichen.anynote.widget

import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import org.wangyichen.anynote.R
import org.wangyichen.anynote.module.AnyNoteApplication.Companion.context
import org.wangyichen.anynote.module.noteDetail.NoteDetailActivity
import org.wangyichen.anynote.module.notes.NotesActivity

object NotificationUtils {
  const val CHANNEL_ID = "CHANNEL_ID"

  fun openNoteNotification(noteId: String) {
    val intent = if (noteId == "") {
      Intent(context, NotesActivity::class.java)
    } else {
      Intent(context, NoteDetailActivity::class.java).apply {
        putExtra(NoteDetailActivity.EXTRA_NOTE_ID, noteId)
      }
    }

    val pendingIntent: PendingIntent =
      PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
      .setSmallIcon(R.mipmap.ic_launcher)
      .setContentTitle("通知提醒")
      .setPriority(NotificationCompat.PRIORITY_DEFAULT)
      .setContentIntent(pendingIntent)
      .setAutoCancel(true)

    with(NotificationManagerCompat.from(context)) {
      notify(noteId.hashCode(), builder.build())
    }
  }
}