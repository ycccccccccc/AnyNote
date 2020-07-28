package org.wangyichen.anynote.data.Entity

import androidx.room.*

@Entity(tableName = "tags")
data class Tag @JvmOverloads constructor(
    @ColumnInfo(name = "name") val name: String = "",
    @ColumnInfo(name = "color") val color: String = "",
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "tagid") val id: Long? = null
)

data class TagWithNotes(
    @Embedded val tag: Tag,
    @Relation(
        parentColumn = "tagid",
        entityColumn = "noteid",
        associateBy = Junction(NoteTagCrossRef::class)
    )
    val notes: List<Note>
)
