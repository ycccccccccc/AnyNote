package org.wangyichen.anynote.source.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import org.wangyichen.anynote.source.Entity.Note
import org.wangyichen.anynote.source.Entity.NoteWithOthers

@Dao
interface NotesDao {

  //  增
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertNote(note: Note)

  //  删
  @Query("delete from notes where noteid = (:noteid)")
  fun deleteNotesById(noteid: List<Long>)

  @Query("delete from notes where noteid = :noteid")
  fun deleteNoteById(noteid: Long)
  //  改
  @Update(onConflict = OnConflictStrategy.REPLACE)
  fun updateNote(note: Note)

  @Query("update notes set trashed = :trashed where noteid in (:noteid)")
  fun updateTrashs(trashed: Boolean, noteid: List<Long>)

  @Query("update notes set trashed = :trashed where noteid =:noteid")
  fun updateTrashById(trashed: Boolean, noteid: Long)


  @Query("update notes set topping = :topping where noteid =:noteid")
  fun updateTopping(topping: Boolean, noteid: Long)

  @Query("update notes set archived = :archived where noteid in (:noteid)")
  fun updateArchiveds(archived: Boolean, noteid: List<Long>)

  @Query("update notes set archived = :archived where noteid =:noteid")
  fun updateArchived(archived: Boolean, noteid: Long)

  @Query("update notes set belongNotebookId = :belongNotebookId where noteid in (:noteid)")
  fun updateBelongNotebookIds(belongNotebookId: Long, noteid: List<Long>)

  //  查
  @Transaction
  @Query("select * from notes")
  fun getNotes(): List<NoteWithOthers>

  @Transaction
  @Query("select * from notes where noteid = :noteid")
  fun getNoteById(noteid: Long): LiveData<NoteWithOthers>

  @Transaction
  @Query("select * from notes where noteid = :noteid")
  fun getNoLiveNoteById(noteid: Long): NoteWithOthers

  @Transaction
  @Query("select * from notes where belongnotebookid = :notebookId")
  fun getNotesByNotebookId(notebookId: Long): LiveData<List<NoteWithOthers>>

  @Transaction
  @Query("select * from notes where trashed = :trashed")
  fun getTrashedNotes(trashed: Boolean): LiveData<List<NoteWithOthers>>

  @Transaction
  @Query("select * from notes where archived = :archived")
  fun getArchivedNotes(archived: Boolean): LiveData<List<NoteWithOthers>>

  @Transaction
  @Query("select * from notes where sketch = :sketch")
  fun getSketchNotes(sketch: Boolean): LiveData<List<NoteWithOthers>>

}