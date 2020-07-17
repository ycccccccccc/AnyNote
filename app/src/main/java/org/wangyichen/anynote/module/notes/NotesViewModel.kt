package org.wangyichen.anynote.module.notes

import android.util.Log
import androidx.lifecycle.*
import org.wangyichen.anynote.module.AnyNoteApplication
import org.wangyichen.anynote.source.Entity.Note
import org.wangyichen.anynote.source.Entity.NoteWithOthers
import org.wangyichen.anynote.source.local.Repository
import org.wangyichen.anynote.utils.IntentUtils
import org.wangyichen.anynote.utils.constant.FilterType
import org.wangyichen.anynote.utils.constant.NotebookIdExt
import org.wangyichen.anynote.utils.constant.SortType
import org.wangyichen.anynote.widget.addEditNotebookDialog.AddEditNotebookDialogFragment
import java.lang.Exception

class NotesViewModel : ViewModel() {
  private val TAG = this.javaClass.toString()
  lateinit var fragment: NotesFragment

  private val repository = Repository.getInstance(AnyNoteApplication.context)
  private var _filter = FilterType.HAS_IMAGE
  private var _sort = SortType.CREATION
  private var _notebookId = NotebookIdExt.ALLNOTES

  private var _notebookName = MutableLiveData<String>()
  val notebookName: LiveData<String>
    get() = _notebookName

  init {
    repository.PREFERENCES.apply {
      _filter = getDefaultFilter()
      _sort = getDefaultSort()
      _notebookId = getDefaultNotebookId()
      _notebookName.value = getDefaultNotebookName()
    }
  }

  private val _notebookType = MutableLiveData<Long>()
  val notebookType: LiveData<Long>
    get() = _notebookType

  private val _openNoteEvent = MutableLiveData<Long>()
  val openNoteEvent: LiveData<Long>
    get() = _openNoteEvent

  private val _addNoteEvent = MutableLiveData<Long>()
  val addNoteEvent: LiveData<Long>
    get() = _addNoteEvent

  private val _snackbarEvent = MutableLiveData<String>()
  val snackbarEvent: LiveData<String>
    get() = _snackbarEvent

  private val _sortChangeEvent = MutableLiveData<Long>()
  val sortChangeEvent: LiveData<Long>
    get() = _sortChangeEvent

  private val _emptyText = MutableLiveData<String>()
  val emptyText: LiveData<String>
    get() = _emptyText

  private val _emptyClickable = MutableLiveData<Boolean>()
  val emptyClickable: LiveData<Boolean>
    get() = _emptyClickable

  private val _fabVisible = MutableLiveData<Boolean>()
  val fabVisible: LiveData<Boolean>
    get() = _fabVisible

  val notebooks = repository.NOTEBOOKS.getNotebooks()

  private val _notes = MutableLiveData<List<NoteWithOthers>>().apply {
    value = emptyList()
  }
  val notes: LiveData<List<NoteWithOthers>>
    get() = _notes

  val isEmpty: LiveData<Boolean> = Transformations.map(_notes) {
    it.isEmpty()
  }

  fun handleActivityResult(requestCode: Int, resultCode: Int) {
    if (IntentUtils.REQUEST_CODE_ADD_EDIT_NOTE_ACTIVITY == requestCode) {
      when (resultCode) {
        IntentUtils.EDIT_RESULT_OK -> _snackbarEvent.setValue(
          "笔记编辑成功"
        )
        IntentUtils.ADD_EDIT_RESULT_OK -> _snackbarEvent.setValue(
          "成功添加笔记"
        )
        IntentUtils.DELETE_RESULT_OK -> _snackbarEvent.setValue(
          "成功删除笔记"
        )
        IntentUtils.ADD_SKETCH_OK -> _snackbarEvent.setValue(
          "成功添加草稿"
        )
      }
    }
  }


  fun load() {
    _notebookType.value = when (_notebookId) {
      NotebookIdExt.ALLNOTES -> NotebookIdExt.ALLNOTES
      NotebookIdExt.ARCHIVED -> NotebookIdExt.ARCHIVED
      NotebookIdExt.SKETCH -> NotebookIdExt.SKETCH
      NotebookIdExt.TRASH -> NotebookIdExt.TRASH
      else -> NotebookIdExt.ALLNOTES
    }
    _emptyText.value = when (_notebookId) {
      NotebookIdExt.ALLNOTES -> "当前没有笔记，点击新建"
      NotebookIdExt.ARCHIVED -> "没有已归档笔记"
      NotebookIdExt.SKETCH -> "草稿箱为空"
      NotebookIdExt.TRASH -> "回收站为空"
      else -> _notebookName.value + "\n没有笔记，点击新建"
    }

    _fabVisible.value = when (_notebookId) {
      NotebookIdExt.ALLNOTES -> true
      NotebookIdExt.ARCHIVED -> false
      NotebookIdExt.SKETCH -> false
      NotebookIdExt.TRASH -> false
      else -> true
    }

    _emptyClickable.value = when (_notebookId) {
      NotebookIdExt.ARCHIVED, NotebookIdExt.SKETCH, NotebookIdExt.TRASH -> false
      else -> true
    }

    val listener = object :
      Repository.LoadListener {
      override fun onSuccess(item: Any) {
        val notes = item as List<NoteWithOthers>
        var tmpNotes = when (_notebookId) {
          NotebookIdExt.ALLNOTES -> notes.filter { !it.note.trashed && !it.note.sketch && !it.note.archived }
          NotebookIdExt.ARCHIVED -> notes.filter { it.note.archived && !it.note.trashed }
          NotebookIdExt.SKETCH -> notes.filter { it.note.sketch && !it.note.trashed }
          NotebookIdExt.TRASH -> notes.filter { it.note.trashed }
          else -> notes.filter { it.note.notebookId == _notebookId && !it.note.trashed && !it.note.sketch && !it.note.archived }
        }
        tmpNotes = when (_sort) {
          SortType.CREATION -> tmpNotes.sortedWith(
            compareBy(
              { !it.note.topping }, { -it.note.creation })
          )
          SortType.LASTMODIFICATION -> tmpNotes.sortedWith(
            compareBy(
              { !it.note.topping }, { -it.note.lastModification })
          )
          else -> tmpNotes
        }
        _notes.postValue(tmpNotes)
        _sortChangeEvent.postValue(_sort)
      }

      override fun onError(e: Exception) {
        Log.e(TAG, e.toString())
      }
    }
    repository.NOTES.getNotes(listener)
  }

  fun start(fragment: NotesFragment) {
    this.fragment = fragment
    load()
  }

  fun onstop() {
    repository.PREFERENCES.apply {
      saveDeafultFilter(_filter)
      saveDefaultNotebookId(_notebookId)
      saveDefaultSort(_sort)
      saveDeafultNotebookName(_notebookName.value!!)
    }
  }

  fun addNewNote() {
    _addNoteEvent.value = _notebookId
  }

  fun openNoteDetails(note: Note) {
    _openNoteEvent.value = note.id
  }

  fun unToppingNote(note: Note) {
    repository.NOTES.untoppingNote(note)
    _snackbarEvent.value = "已取消置顶"
    load()
  }

  fun setSort(sort: Long) {
    _sort = sort
    load()
  }

  fun setFilter(filter: Long) {
    _filter = filter
    load()
  }

  fun openNotebook(notebookId: Long, notebookName: String) {
    if (_notebookId == notebookId) return

    _notebookId = notebookId
    this._notebookName.value = notebookName
    load()
  }

  fun addNewNotebook() {
    AddEditNotebookDialogFragment.getInstance(-1L,fragment.activity!!).show(fragment.parentFragmentManager,TAG)
  }

  fun modifyNotebook(notebookId: Long) {
    AddEditNotebookDialogFragment.getInstance(notebookId,fragment.activity!!).show(fragment.parentFragmentManager,TAG)
  }


  companion object {
    fun getInstance(viewModelStoreOwner: ViewModelStoreOwner) =
      ViewModelProvider(viewModelStoreOwner).get(NotesViewModel::class.java)
  }
}
