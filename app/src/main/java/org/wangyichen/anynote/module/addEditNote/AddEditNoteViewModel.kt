package org.wangyichen.anynote.module.addEditNote

import androidx.lifecycle.*
import com.google.android.material.snackbar.Snackbar
import org.wangyichen.anynote.module.AnyNoteApplication
import org.wangyichen.anynote.source.Entity.Note
import org.wangyichen.anynote.source.Entity.NoteWithOthers
import org.wangyichen.anynote.source.local.Repository
import org.wangyichen.anynote.source.local.repository.NotesRepository
import org.wangyichen.anynote.utils.AppExecutors
import org.wangyichen.anynote.utils.ConfermDialogFragment
import org.wangyichen.anynote.utils.TimeUtils
import org.wangyichen.anynote.utils.ext.showSnackbar
import java.lang.Exception
import java.util.concurrent.CyclicBarrier

class AddEditNoteViewModel : ViewModel() {
  private val repository = Repository.getInstance(AnyNoteApplication.context)
  lateinit var fragment: AddEditNoteFragment

  private var newNote = true

  private val cyclicBarrier = CyclicBarrier(2) {
    setNotebookPos()
  }

  private var _noteId: Long = 0L
  private var _notebookId = 0L
  private var _sketch = false
  private var _creation = 0L
  private var _trash = false
  private var _checkList = false

  val title = MutableLiveData<String>().apply {
    value = ""
  }
  val content = MutableLiveData<String>().apply {
    value = ""
  }
  private var _color = MutableLiveData<Int>()

  val color: LiveData<Int>
    get() = _color
  val notebookPos = MutableLiveData<Int>()

  private val _notebooks = repository.NOTEBOOKS.getNotebooks()
  val notebooks: LiveData<List<String>> = Transformations.map(_notebooks) { notebooks ->
    cyclicBarrier.await()
    notebooks.map { it.name }
  }

  private val _archived = MutableLiveData<Boolean>().apply {
    value = false
  }
  val archived: LiveData<Boolean>
    get() = _archived

  private val _topping = MutableLiveData<Boolean>().apply {
    value = false
  }
  val topping: LiveData<Boolean>
    get() = _topping

  private val _creationTextVisibility = MutableLiveData<Boolean>().apply {
    value = false
  }
  val creationTextVisibility: LiveData<Boolean>
    get() = _creationTextVisibility

  private val _creationText = MutableLiveData<String>()
  val creationText: LiveData<String> = Transformations.map(_creationText) {
    "创建时间：$it"
  }


  private val _alarm = MutableLiveData<Long>().apply {
    value = 0L
  }
  val alarmText: LiveData<String> = Transformations.map(_alarm) {
    if (it == 0L) {
      "点击添加提醒"
    } else if (it < TimeUtils.getTime()) {

      "已提醒于 ${TimeUtils.time2String(it)}"
    } else {
      "将提醒于 ${TimeUtils.time2String(it)}"
    }
  }
  private val _imageUri = MutableLiveData<String>().apply {

  }
  val imageUri: LiveData<String>
    get() = _imageUri

  val alarmShow: LiveData<Int> = Transformations.map(_alarm) {
    if (it == 0L) {
      0 //没有提醒
    } else if (it < TimeUtils.getTime()) {
      1 //已经提醒
    } else {
      2 //未提醒
    }
  }

  private val _deleteNoteEvent = MutableLiveData<Any>()
  val deleteNoteEvent: LiveData<Any>
    get() = _deleteNoteEvent

  private val _showSnackBarEvent = MutableLiveData<String>()
  val showSnackBarEvent: LiveData<String>
    get() = _showSnackBarEvent


  private val _saveNoteEvent = MutableLiveData<Int>()
  val saveNoteEvent: LiveData<Int>
    get() = _saveNoteEvent

  fun cancelEdit() {
    val listener = object : ConfermDialogFragment.ConfermListener {
      override fun onPostive() {
        _saveNoteEvent.postValue(NOCHANGE)
      }

      override fun onNegtive() {
      }
    }
    val title = "是否放弃编辑"
    ConfermDialogFragment(title, "", listener).show(
      fragment.parentFragmentManager,
      this.javaClass.name
    )
  }

  fun saveNote() {
    if (!newNote || !_sketch) {
      if (title.value?.isEmpty()!!) {
        _showSnackBarEvent.value = "标题不能为空"
        return
      }
      if (content.value?.isEmpty()!!) {
        _showSnackBarEvent.value = "内容不能为空"
        return
      }
    }
    val title = this.title.value ?: ""
    val content = this.content.value ?: ""
    val alarm = _alarm.value!!
    val lastModification = TimeUtils.getTime()
    val creation = if (newNote) lastModification else _creation
    val notebookId = _notebookId
    val trashed = _trash
    val checkList = _checkList
    val topping = _topping.value!!
    val archived = _archived.value!!
    val sketch = if (newNote) _sketch else false
    val id = if (newNote) null else _noteId

    val note = Note(
      title,
      content,
      creation,
      lastModification,
      alarm,
      notebookId,
      trashed,
      checkList,
      topping,
      archived,
      sketch,
      id
    )
    repository.NOTES.saveNote(note)
    _saveNoteEvent.value = if (sketch) SKETCH else SAVE
  }

  fun alarmClicked() {
    TODO()
  }

  fun backPressed() {
    if (newNote && title.value == "" && content.value == "") {
      _saveNoteEvent.value = NOCHANGE
      return
    }

    val listener = object : ConfermDialogFragment.ConfermListener {
      override fun onPostive() {
        if (newNote) _sketch = true
        saveNote()
      }

      override fun onNegtive() {
        _saveNoteEvent.value = NOCHANGE
      }
    }

    val title = if (newNote) "是否需要保存草稿" else "是否保存修改"
    ConfermDialogFragment(
      title,
      "",
      listener
    ).show(fragment.parentFragmentManager, javaClass.name)
  }

  fun start(fragment: AddEditNoteFragment, noteId: Long) {
    this.fragment = fragment
    this._noteId = noteId
    notebookPos.observe(fragment.viewLifecycleOwner, Observer {
      _notebookId = _notebooks.value?.get(it)?.id ?: _notebookId
      setColor()
    })


    if (noteId == -1L) {
      newNote = true
      Thread { cyclicBarrier.await() }.start()
    } else {
      newNote = false

      val listener = object : NotesRepository.LoadListener {
        override fun onSuccess(item: Any) {
          val note = item as NoteWithOthers
          title.postValue(note.note.title)
          content.postValue(note.note.content)
          _alarm.postValue(note.note.alarm)
          _notebookId = note.note.notebookId
          _topping.postValue(note.note.topping)
          _archived.postValue(note.note.archived)
          cyclicBarrier.await()
          _creationText.postValue(note.note.createdDateString)
          _creationTextVisibility.postValue(true)
          _creation = note.note.creation
          _trash = note.note.trashed
          _checkList = note.note.checkList
        }

        override fun onError(e: Exception) {
        }
      }
      repository.NOTES.getNoteById(noteId, listener)
    }
  }

  fun changeArchived() {
    val archived = this.archived.value!!
    val listener = object : ConfermDialogFragment.ConfermListener {
      override fun onPostive() {
        _archived.postValue(!archived)
        fragment.view?.showSnackbar("已${if (archived) "取消归档" else "归档"}", Snackbar.LENGTH_SHORT)
      }

      override fun onNegtive() {
      }
    }
    val title = "是否${if (archived) "取消归档" else "归档"}"
    ConfermDialogFragment(title, "", listener).show(
      fragment.parentFragmentManager,
      this.javaClass.name
    )
  }

  fun deleteNote() {
    val listener = object : ConfermDialogFragment.ConfermListener {
      override fun onPostive() {
        if (newNote) {
          _saveNoteEvent.postValue(NOCHANGE)
        } else {
          repository.NOTES.trashNoteById(_noteId)
          _deleteNoteEvent.postValue(Any())
        }
      }

      override fun onNegtive() {
      }
    }
    val title = "是否删除笔记"
    ConfermDialogFragment(title, "", listener).show(
      fragment.parentFragmentManager,
      this.javaClass.name
    )
  }

  fun changeTopping() {
    val topping = this.topping.value!!
    val listener = object : ConfermDialogFragment.ConfermListener {
      override fun onPostive() {
        _topping.postValue(!topping)
        fragment.view?.showSnackbar("已${if (topping) "取消置顶" else "置顶"}", Snackbar.LENGTH_SHORT)
      }

      override fun onNegtive() {
      }
    }
    val title = "是否${if (topping) "取消置顶" else "置顶"}"
    ConfermDialogFragment(title, "", listener).show(
      fragment.parentFragmentManager,
      this.javaClass.name
    )
  }

  private fun setNotebookPos() {
    var pos = 0
    val len = _notebooks.value?.size ?: 0
    for (i in 0 until len) {
      if (_notebooks.value?.get(i)?.id == _notebookId) {
        pos = i
      }
    }
    notebookPos.postValue(pos)
  }

  private fun setColor() {
    if (_notebooks.value != null) {
      for (notebook in _notebooks.value!!) {
        if (notebook.id == _notebookId) {
          _color.postValue(notebook.color)
          break
        }
      }
    }
  }


  companion object {
    const val NOCHANGE = 0
    const val SAVE = 1
    const val SKETCH = 2


    fun newInstance(owner: ViewModelStoreOwner) =
      ViewModelProvider(owner).get(AddEditNoteViewModel::class.java)
  }
}