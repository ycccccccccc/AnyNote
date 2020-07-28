package org.wangyichen.anynote.module.notes

import android.net.Uri
import android.util.Log
import android.view.View
import androidx.lifecycle.*
import org.wangyichen.anynote.R
import org.wangyichen.anynote.module.AnyNoteApplication.Companion.context
import org.wangyichen.anynote.data.Entity.Note
import org.wangyichen.anynote.data.local.Repository
import org.wangyichen.anynote.utils.ConfirmDialogFragment
import org.wangyichen.anynote.utils.IntentUtils
import org.wangyichen.anynote.utils.constant.NotebookIdExt.Companion.ALLNOTES
import org.wangyichen.anynote.utils.constant.NotebookIdExt.Companion.ARCHIVED
import org.wangyichen.anynote.utils.constant.NotebookIdExt.Companion.SKETCH
import org.wangyichen.anynote.utils.constant.NotebookIdExt.Companion.TRASH
import org.wangyichen.anynote.utils.constant.SortType
import org.wangyichen.anynote.widget.addEditNotebookDialog.AddEditNotebookDialogFragment
import org.wangyichen.anynote.widget.notebookChooser.NotebookChooserDialog

class NotesViewModel : ViewModel(), NotesActionModeActionListener {
  lateinit var fragment: NotesFragment
  private val repository = Repository.getInstance(context)
  private var _sort = SortType.CREATION
  private var _notebookId = ALLNOTES
  private lateinit var _actionModeSelectedNotes: Set<Note>
  private val TAG = this.javaClass.toString()


  private val _notes = MutableLiveData<List<Note>>()
  val notes: LiveData<List<Note>>
    get() = _notes

  //  当前笔记本是否含有，影响空页面的展示
  val isEmpty: LiveData<Boolean> = Transformations.map(_notes) {
    it.isEmpty()
  }

  //  action mode 选中项id
  private var _selectedCount = MutableLiveData<String>()
  val selectedCount: LiveData<String>
    get() = _selectedCount

  //  当前笔记本类型
  private val _notebookType = MutableLiveData<Long>()
  val notebookType: LiveData<Long>
    get() = _notebookType

  //  空页面展示文字
  private val _emptyText = MutableLiveData<String>()
  val emptyText: LiveData<String>
    get() = _emptyText

  //  回收站、已归档、草稿箱 空页面不可点击
  private val _emptyClickable = MutableLiveData<Boolean>()
  val emptyClickable: LiveData<Boolean>
    get() = _emptyClickable

  //  fab在action mode 不出现
  private val _fabVisible = MutableLiveData<Boolean>()
  val fabVisible: LiveData<Boolean>
    get() = _fabVisible

  val notebooks = repository.NOTEBOOKS.getNotebooks()

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

  //  笔记本变更或切换笔记本时触发
  private val notebookChangeEvent = MutableLiveData<Any>()
  val notebookName: LiveData<String> = Transformations.map(notebookChangeEvent) {
    if (notebooks.value == null) "" else {
      when (_notebookId) {
        ALLNOTES -> context.getString(R.string.notebook_name_all_note)
        ARCHIVED -> context.getString(R.string.notebook_name_archived)
        SKETCH -> context.getString(R.string.notebook_name_sketch)
        TRASH -> context.getString(R.string.notebook_name_trashed)
        else -> {
          var name = ""
          for (notebook in notebooks.value!!) {
            if (notebook.id == _notebookId) {
              name = notebook.name
              break
            }
          }
          name
        }
      }
    }
  }


  private val _actionMode = MutableLiveData<Boolean>().apply { value = false }
  val actionMode: LiveData<Boolean>
    get() = _actionMode

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

  init {
    repository.PREFERENCES.apply {
      _sort = getDefaultSort()
      _notebookId = getDefaultNotebookId()
    }
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
      else -> notebookName.value + "\n没有笔记，点击新建"
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

    val listener = object : Repository.LoadListener<List<Note>> {
      override fun onSuccess(item: List<Note>) {
//        过滤对应笔记本笔记
        var tmpNotes = when (_notebookId) {
          ALLNOTES -> item.filter { !it.trashed && !it.sketch && !it.archived }
          ARCHIVED -> item.filter { it.archived && !it.trashed }
          SKETCH -> item.filter { it.sketch && !it.trashed }
          TRASH -> item.filter { it.trashed }
          else -> item.filter { it.notebookId == _notebookId && !it.trashed && !it.sketch && !it.archived }
        }
//        根据sort类型排序，同时保证置顶
        tmpNotes = when (_sort) {
          SortType.CREATION -> tmpNotes.sortedWith(
            compareBy({ !it.topping }, { -it.creation })
          )
          SortType.LASTMODIFICATION -> tmpNotes.sortedWith(
            compareBy({ !it.topping }, { -it.lastModification })
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
    notebooks.observe(fragment.viewLifecycleOwner, Observer {
      notebookChangeEvent.value = Any()
    })
    load()
  }

  fun onstop() {
    repository.PREFERENCES.apply {
      saveDefaultNotebookId(_notebookId)
      saveDefaultSort(_sort)
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
    _notebookType.value = _notebookType.value // 触发回收站菜单选项展示
  }

  fun onSelectedNotesChanged(notes: Set<Note>) {
    _actionModeSelectedNotes = notes
    val count = notes.size
    _selectedCount.value = "已选择 ${if (count > 999) "999+" else count} 条笔记"
  }

  fun unToppingNote(note: Note) {
    val listener = object : ConfirmDialogFragment.ConfirmListener {
      override fun onPositive() {
        repository.NOTES.untoppingNote(note)
        _snackbarEvent.value = "已取消置顶"
        load()
      }

      override fun onNegative() {}
    }
    ConfirmDialogFragment(
      "是否取消置顶",
      "",
      listener
    ).show(fragment.parentFragmentManager, TAG)
  }

  fun setSort(sort: Long) {
    _sort = sort
    load()
  }

//  切换笔记本
  fun openNotebook(notebookId: Long) {
    if (_notebookId == notebookId) return
    _notebookId = notebookId
    notebookChangeEvent.value = Any()
    load()
  }

  fun clearTrash() {
    if (notes.value?.size == 0) {
      _snackbarEvent.value = "回收站为空"
      return
    }
    val listener = object : ConfirmDialogFragment.ConfirmListener {
      override fun onPositive() {
        repository.COVER.deleteImages(notes.value!!.filter { it.trashed }
          .map { Uri.parse(it.coverImage) })
        repository.NOTES.clearNotes()
        load()
      }

      override fun onNegative() {}
    }
    ConfirmDialogFragment("清空回收站", "确定清空回收站？此过程不可逆！", listener).show(
      fragment.parentFragmentManager,
      TAG
    )
  }

  fun addNewNotebook() {
    AddEditNotebookDialogFragment.getInstance(-1L, fragment.activity!!)
      .show(fragment.parentFragmentManager, TAG)
  }

  fun modifyNotebook(notebookId: Long) {
    //  当笔记本被删除时，刷新。当前笔记本被删除，跳转 全部笔记
    val listener = object : AddEditNotebookDialogFragment.DeleteListener {
      override fun onDelete() {
        if (notebookId == _notebookId) {
          openNotebook(ALLNOTES)
        } else {
          load()
        }
      }
    }
    AddEditNotebookDialogFragment.getInstance(notebookId, fragment.activity!!, listener)
      .show(fragment.parentFragmentManager, TAG)
  }

//  action mode 菜单事件
  override fun onActionTopping() {
    if (_actionModeSelectedNotes.isEmpty()) {
      _snackbarEvent.value = "未选择笔记"
      return
    }
    val listener = object : ConfirmDialogFragment.ConfirmListener {
      override fun onPositive() {
        repository.NOTES.toppingNotes(_actionModeSelectedNotes.toList().map { it.id })
        _actionMode.value = false
        load()
      }

      override fun onNegative() {}
    }
    val title = "批量置顶"
    val content = "批量置顶${_actionModeSelectedNotes.size}条笔记？"
    ConfirmDialogFragment.show(title, content, listener, fragment.parentFragmentManager, TAG)
  }

  override fun onActionArchive() {
    if (_actionModeSelectedNotes.isEmpty()) {
      _snackbarEvent.value = "未选择笔记"
      return
    }
    val listener = object : ConfirmDialogFragment.ConfirmListener {
      override fun onPositive() {
        repository.NOTES.changeNotesarchive(_actionModeSelectedNotes.toList().map { it.id }, true)
        _actionMode.value = false
        load()
      }

      override fun onNegative() {}
    }
    val title = "批量归档"
    val content = "批量归档${_actionModeSelectedNotes.size}条笔记？"
    ConfirmDialogFragment.show(title, content, listener, fragment.parentFragmentManager, TAG)
  }

  override fun onActionUnarchive() {
    if (_actionModeSelectedNotes.isEmpty()) {
      _snackbarEvent.value = "未选择笔记"
      return
    }
    val listener = object : ConfirmDialogFragment.ConfirmListener {
      override fun onPositive() {
        repository.NOTES.changeNotesarchive(_actionModeSelectedNotes.toList().map { it.id }, false)
        _actionMode.value = false
        load()
      }

      override fun onNegative() {}
    }
    val title = "批量取消归档"
    val content = "批量取消归档${_actionModeSelectedNotes.size}条笔记？"
    ConfirmDialogFragment.show(title, content, listener, fragment.parentFragmentManager, TAG)
  }

  override fun onActionDelete() {
    if (_actionModeSelectedNotes.isEmpty()) {
      _snackbarEvent.value = "未选择笔记"
      return
    }
    val listener = object : ConfirmDialogFragment.ConfirmListener {
      override fun onPositive() {
        repository.NOTES.trashNotes(_actionModeSelectedNotes.toList().map { it.id })
        _actionMode.value = false
        load()
      }

      override fun onNegative() {}
    }
    val title = "批量删除"
    val content = "批量删除${_actionModeSelectedNotes.size}条笔记？"
    ConfirmDialogFragment.show(title, content, listener, fragment.parentFragmentManager, TAG)
  }

  override fun onActionChangeNotebook() {
    if (_actionModeSelectedNotes.isEmpty()) {
      _snackbarEvent.value = "未选择笔记"
      return
    }
    val listener = object : NotebookChooserDialog.ConfirmListener {
      override fun onPositive(notebookId: Long) {
        repository.NOTES.changeNotebookIds(
          _actionModeSelectedNotes.toList().map { it.id },
          notebookId
        )
        _actionMode.value = false
        load()
      }
    }
    NotebookChooserDialog.show(
      _actionModeSelectedNotes.size,
      notebooks.value!!,
      listener,
      fragment.parentFragmentManager,
      TAG
    )
  }

  override fun onActionClear() {
    if (_actionModeSelectedNotes.isEmpty()) {
      _snackbarEvent.value = "未选择笔记"
      return
    }
    val listener = object : ConfirmDialogFragment.ConfirmListener {
      override fun onPositive() {
        repository.NOTES.deleteNotesById(_actionModeSelectedNotes.toList().map { it.id })
        repository.COVER.deleteImages(_actionModeSelectedNotes.map { Uri.parse(it.coverImage) })
        _actionMode.value = false
        load()
      }

      override fun onNegative() {}
    }
    val title = "彻底删除"
    val content = "彻底删除${_actionModeSelectedNotes.size}条笔记？此过程不可逆!"
    ConfirmDialogFragment.show(title, content, listener, fragment.parentFragmentManager, TAG)
  }

  override fun onActionRestore() {
    if (_actionModeSelectedNotes.isEmpty()) {
      _snackbarEvent.value = "未选择笔记"
      return
    }
    val listener = object : ConfirmDialogFragment.ConfirmListener {
      override fun onPositive() {
        repository.NOTES.untrashNotes(_actionModeSelectedNotes.toList().map { it.id })
        _actionMode.value = false
        load()
      }

      override fun onNegative() {}
    }
    val title = "批量恢复"
    val content = "批量恢复${_actionModeSelectedNotes.size}条笔记？"
    ConfirmDialogFragment.show(title, content, listener, fragment.parentFragmentManager, TAG)
  }

  companion object {
    fun getInstance(viewModelStoreOwner: ViewModelStoreOwner) =
      ViewModelProvider(viewModelStoreOwner).get(NotesViewModel::class.java)
  }
}
