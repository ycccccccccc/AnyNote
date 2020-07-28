package org.wangyichen.anynote.data.Entity

import android.graphics.Color
import androidx.room.*

@Entity(tableName = "notebooks")
data class Notebook @JvmOverloads constructor(
  @ColumnInfo(name = "name") var name: String = "",
  @ColumnInfo(name = "color") var color: Int = Color.WHITE,
  @ColumnInfo(name = "description") var description: String = "",
  @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "notebookid") val id: Long? = null
)


data class NotebookWithNotes(
    @Embedded val notebook: Notebook,
    @Relation(
        parentColumn = "notebookid",
        entityColumn = "belongnotebookid")
    val notes: List<Note>
)