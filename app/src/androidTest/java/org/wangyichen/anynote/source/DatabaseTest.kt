package org.wangyichen.anynote.source

import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleRegistry
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.wangyichen.anynote.source.Entity.Note
import org.wangyichen.anynote.source.Entity.Notebook
import org.wangyichen.anynote.source.local.NoteDatabase
import org.wangyichen.anynote.utils.AppExecutors

@RunWith(AndroidJUnit4::class)
class DatabaseTest {
  lateinit var database: NoteDatabase

  @Before
  fun init() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    database = Room.inMemoryDatabaseBuilder(
      context, NoteDatabase::class.java
    ).build()
  }

  @After
  fun destory() {
    database.close()
  }

  @Test
  fun insertNote() {
//    ???
    var notes = database.notesDao().getNotes()
    AppExecutors.getInstance().mainThread.execute {
      notes.observeForever { list ->
        Log.d("ttt", "${list.size}")
      }
    }
  }

  companion object {
    val DEFAULT_TITLE = "title"
    val DEFAULT_CONTENT = "content"
    val DEFAULT_NOTEBOOKID = 1L
    val DEFAULT_NOTE =
      Note(title = DEFAULT_TITLE, content = DEFAULT_CONTENT, notebookId = DEFAULT_NOTEBOOKID)
  }
}