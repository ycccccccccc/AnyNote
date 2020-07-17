package org.wangyichen.anynote.source.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import org.wangyichen.anynote.source.Entity.Notebook
import org.wangyichen.anynote.source.Entity.NotebookWithNotes

@Dao
interface NotebooksDao {
  //增
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertNotebook(notebook: Notebook)

  //  删
  @Query("delete from notebooks where notebookid = :notebookId")
  fun deleteNotebookById(notebookId: Long)

  //  改
  @Update
  fun updateNotebook(notebook: Notebook)

  //  查
  @Query("select * from notebooks")
  fun getNotebooks(): LiveData<List<Notebook>>

  @Query("select * from notebooks where notebookid = :notebookId")
  fun getNotebookById(notebookId: Long): LiveData<Notebook>

  @Query("select * from notebooks where notebookid = :notebookId")
  fun getNoLiveNotebookById(notebookId: Long): Notebook
}