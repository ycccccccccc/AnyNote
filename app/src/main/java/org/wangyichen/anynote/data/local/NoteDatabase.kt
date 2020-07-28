package org.wangyichen.anynote.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import org.wangyichen.anynote.data.Entity.Note
import org.wangyichen.anynote.data.Entity.Notebook
import org.wangyichen.anynote.data.local.dao.NotebooksDao
import org.wangyichen.anynote.data.local.dao.NotesDao

//@Database(entities = [Notebook::class, Attachment::class, Note::class, Tag::class], version = 1)
@Database(
  entities = [Note::class, Notebook::class],
  version = 1
)
abstract class NoteDatabase : RoomDatabase() {
  abstract fun notesDao(): NotesDao
  abstract fun notebooksDao(): NotebooksDao

  companion object {

    private var INSTANCE: NoteDatabase? = null

    private val lock = Any()

    fun getInstance(context: Context): NoteDatabase {
      synchronized(lock) {
        if (INSTANCE == null) {
          INSTANCE = Room.databaseBuilder(
            context.applicationContext,
            NoteDatabase::class.java, "AnyNote.db"
          )
            .build()
        }
        return INSTANCE!!
      }
    }
  }
}