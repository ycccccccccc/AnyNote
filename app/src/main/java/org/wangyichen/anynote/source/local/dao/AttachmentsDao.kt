package org.wangyichen.anynote.source.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import org.wangyichen.anynote.source.Entity.Attachment

@Dao
interface AttachmentsDao {
  //  增
  @Insert
  fun insertAttachment(attachment: Attachment)

  //  删
  @Delete
  fun deleteAttachment(attachment: Attachment)

  //  改
  @Update
  fun updateAttachment(attachment: Attachment)

  //  查
  @Query("select * from attachments where attachmentid = :attachmentId")
  fun getAttachmentById(attachmentId: Long): LiveData<List<Attachment>>
}