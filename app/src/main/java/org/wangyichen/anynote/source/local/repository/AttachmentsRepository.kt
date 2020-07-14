package org.wangyichen.anynote.source.local.repository

import android.content.Context
import org.wangyichen.anynote.source.Entity.Attachment
import org.wangyichen.anynote.source.local.NoteDatabase
import org.wangyichen.anynote.utils.AppExecutors

class AttachmentsRepository private constructor(
    val database: NoteDatabase,
    val executors: AppExecutors
) {
  private val dao = database.attachmentsDao()

  fun saveAttachment(attachment: Attachment) {
    executors.diskIO.execute {
      dao.insertAttachment(attachment)
    }
  }

  fun deleteAttachment(attachment: Attachment) {
    executors.diskIO.execute {
      dao.deleteAttachment(attachment)
    }
  }

  fun updateAttachment(attachment: Attachment) {
    executors.diskIO.execute {
      dao.updateAttachment(attachment)
    }
  }


  companion object {
    private var INSTANCE: AttachmentsRepository? = null
    private val lock = Any()
    fun getInstance(context: Context): AttachmentsRepository {
      synchronized(lock) {
        if (INSTANCE == null) {
          INSTANCE = AttachmentsRepository(NoteDatabase.getInstance(context), AppExecutors.getInstance())
        }
        return INSTANCE !!
      }
    }
  }
}