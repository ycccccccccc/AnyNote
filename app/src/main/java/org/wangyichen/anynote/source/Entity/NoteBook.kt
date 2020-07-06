package org.wangyichen.anynote.source.Entity

import androidx.annotation.VisibleForTesting
import androidx.room.*

@Entity(tableName = "notebooks")
data class Notebook @JvmOverloads constructor(
    @ColumnInfo(name = "name") val name: String = "",
    @ColumnInfo(name = "color") val color: String = "",
    @ColumnInfo(name = "description") val description: String = "",
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "notebookid") val id: Long? = null
)


data class NotebookWithNotes(
    @Embedded val notebook: Notebook,
    @Relation(
        parentColumn = "notebookid",
        entityColumn = "belongnotebookid")
    val notes: List<Note>
)