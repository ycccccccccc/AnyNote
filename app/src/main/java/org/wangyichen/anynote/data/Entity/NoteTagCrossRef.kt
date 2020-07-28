package org.wangyichen.anynote.data.Entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["noteid","tagid"])
data class NoteTagCrossRef(
    @ColumnInfo(name = "tagid") val tagId: Long,
    @ColumnInfo(name = "noteid") val noteId: String
)