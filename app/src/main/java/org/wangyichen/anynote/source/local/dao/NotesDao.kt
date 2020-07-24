package org.wangyichen.anynote.source.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import org.wangyichen.anynote.source.Entity.Note

@Dao
interface NotesDao {

  //  增
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertNote(note: Note)

  //  删
  @Query("delete from notes where noteid = (:noteid)")
  fun deleteNotesById(noteid: List<String>)

  @Query("delete from notes where trashed = :trashed")
  fun deleteNotes(trashed: Boolean)

  @Query("delete from notes where noteid = :noteid")
  fun deleteNoteById(noteid: String)

  //  改
  @Update(onConflict = OnConflictStrategy.REPLACE)
  fun updateNote(note: Note)

  @Query("update notes set trashed = :trashed where noteid in (:noteid)")
  fun updateTrashs(trashed: Boolean, noteid: List<String>)

  @Query("update notes set trashed = :trashed where noteid =:noteid")
  fun updateTrashById(trashed: Boolean, noteid: String)

  @Query("update notes set trashed = :trashed where belongnotebookid =:notebookid")
  fun updateTrashByNotebookId(trashed: Boolean, notebookid: Long)

  @Query("update notes set topping = :topping where noteid =:noteid")
  fun updateToppingById(topping: Boolean, noteid: String)

  @Query("update notes set topping = :topping where noteid in(:noteids)")
  fun toppingNotes(topping: Boolean, noteids: List<String>)

  @Query("update notes set alarm = :alarm where noteid =:noteid")
  fun updateAlarm(alarm: Long, noteid: String)

  @Query("update notes set belongnotebookid = :newNotebookId where belongnotebookid =:oldNotebookId")
  fun updateNotebookId(oldNotebookId: Long, newNotebookId: Long)

  @Query("update notes set archived = :archived where noteid in (:noteid)")
  fun updateArchiveds(archived: Boolean, noteid: List<String>)

  @Query("update notes set archived = :archived where noteid =:noteid")
  fun updateArchived(archived: Boolean, noteid: String)

  @Query("update notes set belongNotebookId = :belongNotebookId where noteid in (:noteid)")
  fun updateBelongNotebookIds(belongNotebookId: Long, noteid: List<String>)

  //  查
  @Transaction
  @Query("select * from notes")
  fun getNotes(): List<Note>

  @Transaction
  @Query("select * from notes where noteid = :noteid")
  fun getNoteById(noteid: String): LiveData<Note>

  @Transaction
  @Query("select * from notes where noteid = :noteid")
  fun getNoLiveNoteById(noteid: String): Note

  @Transaction
  @Query("select * from notes where belongnotebookid = :notebookId")
  fun getNotesByNotebookId(notebookId: Long): LiveData<List<Note>>

  @Transaction
  @Query("select * from notes where trashed = :trashed")
  fun getTrashedNotes(trashed: Boolean): LiveData<List<Note>>

  @Transaction
  @Query("select * from notes where archived = :archived")
  fun getArchivedNotes(archived: Boolean): LiveData<List<Note>>

  @Transaction
  @Query("select * from notes where sketch = :sketch")
  fun getSketchNotes(sketch: Boolean): LiveData<List<Note>>

}