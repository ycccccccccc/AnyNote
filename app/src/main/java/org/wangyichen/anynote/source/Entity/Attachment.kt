package org.wangyichen.anynote.source.Entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attachments")
data class Attachment @JvmOverloads constructor(
    @ColumnInfo(name = "name") val name: String = "",
    @ColumnInfo(name = "uri") val uri: String = "",
    @ColumnInfo(name = "mimetype") val type: String = "",
    @ColumnInfo(name = "size") val size: Long = 0,
    @ColumnInfo(name = "length") val length: Long = 0,
    @ColumnInfo(name = "belongnoteid") val nodeId: Long = 0,
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "attachmentid") val id: Long? = null
)