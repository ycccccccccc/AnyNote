package org.wangyichen.anynote.source.Entity

import androidx.room.*
import org.wangyichen.anynote.utils.TimeUtils
import java.util.*

@Entity(
  tableName = "notes",
  foreignKeys = [ForeignKey(
    entity = Notebook::class,
    parentColumns = ["notebookid"],
    childColumns = ["belongnotebookid"]
  )],
  indices = [Index(value = ["belongnotebookid"])]
)
data class Note @JvmOverloads constructor(
  @ColumnInfo(name = "title") val title: String = "",                                  // title
  @ColumnInfo(name = "content") val content: String = "",                              // content
  @ColumnInfo(name = "creation") val creation: Long = 0,                               // 创建时间
  @ColumnInfo(name = "lastmodification") val lastModification: Long = 0,               // 修改时间
  @ColumnInfo(name = "coverimage") val coverImage: String = "",                        // 封面
  @ColumnInfo(name = "alarm") val alarm: Long = 0,                                     // 提醒时间
  @ColumnInfo(name = "belongnotebookid") val notebookId: Long = 0,                     // 所属笔记本
  @ColumnInfo(name = "trashed") val trashed: Boolean = false,                          // 是否删除
  @ColumnInfo(name = "checklist") val checkList: Boolean = false,                      // 是否待办事项 0不是 1是
  @ColumnInfo(name = "topping") val topping: Boolean = false,                          // 是否置顶
  @ColumnInfo(name = "archived") val archived: Boolean = false,                        // 是否归档
  @ColumnInfo(name = "sketch") val sketch: Boolean = false,                            // 是否草稿
  @PrimaryKey() @ColumnInfo(name = "noteid") val id: String = UUID.randomUUID().toString() // id
) {
  val reminderFired: Boolean //是否已响铃
    get() = alarm < TimeUtils.getTime()
  val createdDate: List<Int>
    get() = TimeUtils.time2Date(creation)
  val modificatedDate: List<Int>
    get() = TimeUtils.time2Date(lastModification)
  val createdDateString: String
    get() = TimeUtils.time2String(creation)
  val modificatedDateString: String
    get() = TimeUtils.time2String(lastModification)
  val alarmString: String
    get() = TimeUtils.time2String(alarm)
}
