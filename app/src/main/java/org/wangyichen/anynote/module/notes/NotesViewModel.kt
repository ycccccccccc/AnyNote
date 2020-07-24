package org.wangyichen.anynote.module.notes

import android.net.Uri
import android.util.Log
import android.view.View
import androidx.lifecycle.*
import org.wangyichen.anynote.module.AnyNoteApplication
import org.wangyichen.anynote.source.Entity.Note
import org.wangyichen.anynote.source.local.Repository
import org.wangyichen.anynote.utils.ConfermDialogFragment
import org.wangyichen.anynote.utils.IntentUtils
import org.wangyichen.anynote.utils.constant.FilterType
import org.wangyichen.anynote.utils.constant.NotebookIdExt.Companion.ALLNOTES
import org.wangyichen.anynote.utils.constant.NotebookIdExt.Companion.ARCHIVED
import org.wangyichen.anynote.utils.constant.NotebookIdExt.Companion.SKETCH
import org.wangyichen.anynote.utils.constant.NotebookIdExt.Companion.TRASH
import org.wangyichen.anynote.utils.constant.SortType
import org.wangyichen.anynote.widget.notebookChooser.NotebookChooserDialog
import org.wangyichen.anynote.widget.addEditNotebookDialog.AddEditNotebookDialogFragment

class NotesViewModel : ViewModel(), NotesActionModeActionListener {
  private val TAG = this.javaClass.toString()
  lateinit var fragment: NotesFragment

  private val repository = Repository.getInstance(AnyNoteApplication.context)
  private var _filter = FilterType.HAS_IMAGE
  private var _sort = SortType.CREATION
  private var _notebookId = ALLNOTES
  private lateinit var _actionModeSeletedNotes: Set<Note>

  private var _notebookName = MutableLiveData<String>()
  val notebookName: LiveData<String>
    get() = _notebookName

  private var _selectedCount = MutableLiveData<String>()
  val selectedCount: LiveData<String>
    get() = _selectedCount

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

  private val _openNoteEvent = MutableLiveData<String>()
  val openNoteEvent: LiveData<String>
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

  private val _notes = MutableLiveData<List<Note>>().apply {
    value = emptyList()
  }
  val notes: LiveData<List<Note>>
    get() = _notes

  val isEmpty: LiveData<Boolean> = Transformations.map(_notes) {
    it.isEmpty()
  }

  //  action menu item 可见性
  val actionModeToppingVisibility: LiveData<Int> = Transformations.map(_notebookType) {
    if (it == ALLNOTES) View.VISIBLE else View.GONE
  }
  val actionModeArchiveVisibility: LiveData<Int> = Transformations.map(_notebookType) {
    if (it == ALLNOTES) View.VISIBLE else View.GONE
  }
  val actionModeUnarchiveVisibility: LiveData<Int> = Transformations.map(_notebookType) {
    if (it == ARCHIVED) View.VISIBLE else View.GONE
  }
  val actionModeDeleteVisibility: LiveData<Int> = Transformations.map(_notebookType) {
    if (it != TRASH) View.VISIBLE else View.GONE
  }
  val actionModeClearVisibility: LiveData<Int> = Transformations.map(_notebookType) {
    if (it == TRASH) View.VISIBLE else View.GONE
  }
  val actionModeMoveVisibility: LiveData<Int> = Transformations.map(_notebookType) {
    if (it == ALLNOTES) View.VISIBLE else View.GONE
  }
  val actionModeRestoreVisibility: LiveData<Int> = Transformations.map(_notebookType) {
    if (it == TRASH) View.VISIBLE else View.GONE
  }
  private val _actionMode = MutableLiveData<Boolean>().apply { value = false }
  val actionMode: LiveData<Boolean>
    get() = _actionMode

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
      ALLNOTES -> ALLNOTES
      ARCHIVED -> ARCHIVED
      SKETCH -> SKETCH
      TRASH -> TRASH
      else -> ALLNOTES
    }
    _emptyText.value = when (_notebookId) {
      ALLNOTES -> "当前没有笔记，点击新建"
      ARCHIVED -> "没有已归档笔记"
      SKETCH -> "草稿箱为空"
      TRASH -> "回收站为空"
      else -> _notebookName.value + "\n没有笔记，点击新建"
    }

    _fabVisible.value = when (_notebookId) {
      ALLNOTES -> true
      ARCHIVED -> false
      SKETCH -> false
      TRASH -> false
      else -> true
    }

    _emptyClickable.value = when (_notebookId) {
      ARCHIVED, SKETCH, TRASH -> false
      else -> true
    }

    val listener = object :
      Repository.LoadListener<List<Note>> {
      override fun onSuccess(item: List<Note>) {
        var tmpNotes = when (_notebookId) {
          ALLNOTES -> item.filter { !it.trashed && !it.sketch && !it.archived }
          ARCHIVED -> item.filter { it.archived && !it.trashed }
          SKETCH -> item.filter { it.sketch && !it.trashed }
          TRASH -> item.filter { it.trashed }
          else -> item.filter { it.notebookId == _notebookId && !it.trashed && !it.sketch && !it.archived }
        }
        tmpNotes = when (_sort) {
          SortType.CREATION -> tmpNotes.sortedWith(
            compareBy(
              { !it.topping }, { -it.creation })
          )
          SortType.LASTMODIFICATION -> tmpNotes.sortedWith(
            compareBy(
              { !it.topping }, { -it.lastModification })
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

  fun startActionMode() {
    _actionMode.value = true
    _fabVisible.value = false
  }

  fun closeActionMode() {
    _actionMode.value = false
    _fabVisible.value = when (_notebookId) {
      ALLNOTES -> true
      ARCHIVED -> false
      SKETCH -> false
      TRASH -> false
      else -> true
    }
    _notebookType.value = _notebookType.value
  }

  fun onSelectedNotesChanged(notes: Set<Note>) {
    _actionModeSeletedNotes = notes
    val count = notes.size
    _selectedCount.value = "已选择 ${if (count > 999) "999+" else count} 条笔记"
  }

  fun unToppingNote(note: Note) {
    val listener = object : ConfermDialogFragment.ConfermListener {
      override fun onPositive() {
        repository.NOTES.untoppingNote(note)
        _snackbarEvent.value = "已取消置顶"
        load()
      }

      override fun onNegtive() {
      }
    }
    ConfermDialogFragment(
      "是否取消置顶",
      "",
      listener
    ).show(fragment.parentFragmentManager, TAG)
  }

  fun setSort(sort: Long) {
    _sort = sort
    load()
  }


  fun openNotebook(notebookId: Long, notebookName: String) {
    if (_notebookId == notebookId) return

    _notebookId = notebookId
    this._notebookName.value = notebookName
    load()
  }

  fun clearTrash() {
    if (notes.value?.size == 0) {
      _snackbarEvent.value = "回收站为空"
      return
    }
    val listener = object : ConfermDialogFragment.ConfermListener {
      override fun onPositive() {
        repository.COVER.deleteImages(notes.value!!.filter { it.trashed }
          .map { Uri.parse(it.coverImage) })
        repository.NOTES.deleteNotes()
        load()
      }

      override fun onNegtive() {
      }
    }
    ConfermDialogFragment("清空回收站", "确定清空回收站？此过程不可逆！", listener).show(
      fragment.parentFragmentManager,
      TAG
    )
  }

  fun addNewNotebook() {
    AddEditNotebookDialogFragment.getInstance(-1L, fragment.activity!!)
      .show(fragment.parentFragmentManager, TAG)
  }

  fun modifyNotebook(notebookId: Long) {
    AddEditNotebookDialogFragment.getInstance(notebookId, fragment.activity!!)
      .show(fragment.parentFragmentManager, TAG)
  }

  //  action mode 菜单事件
  override fun onActionTopping() {
    if (_actionModeSeletedNotes.isEmpty()) {
      _snackbarEvent.value = "未选择笔记"
      return
    }
    val listener = object : ConfermDialogFragment.ConfermListener {
      override fun onPositive() {
        repository.NOTES.toppingNotes(_actionModeSeletedNotes.toList().map { it.id })
        _actionMode.value = false
        load()
      }

      override fun onNegtive() {}
    }
    val title = "批量置顶"
    val content = "批量置顶${_actionModeSeletedNotes.size}条笔记？"
    ConfermDialogFragment.show(title, content, listener, fragment.parentFragmentManager, TAG)
  }

  override fun onActionArchive() {
    if (_actionModeSeletedNotes.isEmpty()) {
      _snackbarEvent.value = "未选择笔记"
      return
    }
    val listener = object : ConfermDialogFragment.ConfermListener {
      override fun onPositive() {
        repository.NOTES.changeNotesarchive(_actionModeSeletedNotes.toList().map { it.id }, true)
        _actionMode.value = false
        load()
      }

      override fun onNegtive() {}
    }
    val title = "批量归档"
    val content = "批量归档${_actionModeSeletedNotes.size}条笔记？"
    ConfermDialogFragment.show(title, content, listener, fragment.parentFragmentManager, TAG)
  }

  override fun onActionUnarchive() {
    if (_actionModeSeletedNotes.isEmpty()) {
      _snackbarEvent.value = "未选择笔记"
      return
    }
    val listener = object : ConfermDialogFragment.ConfermListener {
      override fun onPositive() {
        repository.NOTES.changeNotesarchive(_actionModeSeletedNotes.toList().map { it.id }, false)
        _actionMode.value = false
        load()
      }

      override fun onNegtive() {}
    }
    val title = "批量取消归档"
    val content = "批量取消归档${_actionModeSeletedNotes.size}条笔记？"
    ConfermDialogFragment.show(title, content, listener, fragment.parentFragmentManager, TAG)
  }

  override fun onActionDelete() {
    if (_actionModeSeletedNotes.isEmpty()) {
      _snackbarEvent.value = "未选择笔记"
      return
    }
    val listener = object : ConfermDialogFragment.ConfermListener {
      override fun onPositive() {
        repository.NOTES.trashNotes(_actionModeSeletedNotes.toList().map { it.id })
        _actionMode.value = false
        load()
      }

      override fun onNegtive() {}
    }
    val title = "批量删除"
    val content = "批量删除${_actionModeSeletedNotes.size}条笔记？"
    ConfermDialogFragment.show(title, content, listener, fragment.parentFragmentManager, TAG)
  }

  override fun onActionChangeNotebook() {
    if (_actionModeSeletedNotes.isEmpty()) {
      _snackbarEvent.value = "未选择笔记"
      return
    }
    val listener = object : NotebookChooserDialog.ConfermListener {
      override fun onPositive(notebookId: Long) {
        repository.NOTES.changeNotebookIds(
          _actionModeSeletedNotes.toList().map { it.id },
          notebookId
        )
        _actionMode.value = false
        load()
      }
    }
    NotebookChooserDialog.show(
      _actionModeSeletedNotes.size,
      notebooks.value!!,
      listener,
      fragment.parentFragmentManager,
      TAG
    )
  }

  override fun onActionClear() {
    if (_actionModeSeletedNotes.isEmpty()) {
      _snackbarEvent.value = "未选择笔记"
      return
    }
    val listener = object : ConfermDialogFragment.ConfermListener {
      override fun onPositive() {
        repository.NOTES.deleteNotesById(_actionModeSeletedNotes.toList().map { it.id })
        repository.COVER.deleteImages(_actionModeSeletedNotes.map { Uri.parse(it.coverImage) })
        _actionMode.value = false
        load()
      }

      override fun onNegtive() {}
    }
    val title = "彻底删除"
    val content = "彻底删除${_actionModeSeletedNotes.size}条笔记？此过程不可逆!"
    ConfermDialogFragment.show(title, content, listener, fragment.parentFragmentManager, TAG)
  }

  override fun onActionRestore() {
    if (_actionModeSeletedNotes.isEmpty()) {
      _snackbarEvent.value = "未选择笔记"
      return
    }
    val listener = object : ConfermDialogFragment.ConfermListener {
      override fun onPositive() {
        repository.NOTES.untrashNotes(_actionModeSeletedNotes.toList().map { it.id })
        _actionMode.value = false
        load()
      }

      override fun onNegtive() {}
    }
    val title = "批量恢复"
    val content = "批量恢复${_actionModeSeletedNotes.size}条笔记？"
    ConfermDialogFragment.show(title, content, listener, fragment.parentFragmentManager, TAG)
  }

  companion object {
    fun getInstance(viewModelStoreOwner: ViewModelStoreOwner) =
      ViewModelProvider(viewModelStoreOwner).get(NotesViewModel::class.java)
  }
}
