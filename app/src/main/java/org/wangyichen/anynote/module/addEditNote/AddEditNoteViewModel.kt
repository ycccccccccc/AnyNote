package org.wangyichen.anynote.module.addEditNote

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import org.wangyichen.anynote.Event
import org.wangyichen.anynote.module.AnyNoteApplication
import org.wangyichen.anynote.source.Entity.Note
import org.wangyichen.anynote.source.local.Repository
import org.wangyichen.anynote.source.local.repository.CoverRepository
import org.wangyichen.anynote.utils.ConfermDialogFragment
import org.wangyichen.anynote.utils.IntentUtils
import org.wangyichen.anynote.utils.ReminderUtils
import org.wangyichen.anynote.utils.TimeUtils
import org.wangyichen.anynote.utils.ext.showSnackbar
import org.wangyichen.anynote.widget.EditCoverDialog
import org.wangyichen.anynote.widget.editReminderDialog.EditReminderDialog
import java.lang.Exception
import java.util.*
import java.util.concurrent.CyclicBarrier

class AddEditNoteViewModel : ViewModel() {
  private val TAG = this.javaClass.name
  private val repository = Repository.getInstance(AnyNoteApplication.context)
  lateinit var fragment: AddEditNoteFragment

  private var newNote = true
  private var spinnerInited = false
  private var inited = false
  private var _initCoverUri = Uri.EMPTY
  private var _initSketch = false

  private val cyclicBarrier = CyclicBarrier(3) {
    setNotebookPos()
  }

  private var _noteId: String = ""
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
    when {
      it == 0L -> "点击添加提醒"
      it < TimeUtils.getTime() -> "已提醒于 ${TimeUtils.time2String(it)}"
      else -> "将提醒于 ${TimeUtils.time2String(it)}"
    }
  }

  private val _imageUri = MutableLiveData<Uri>().apply {
    value = Uri.EMPTY
  }

  val imageUri: LiveData<Uri>
    get() = _imageUri

  private val _hasImage = MutableLiveData<Boolean>()
  val hasImage: LiveData<Boolean>
    get() = _hasImage

  val alarmShow: LiveData<Int> = Transformations.map(_alarm) {
    when {
      it == 0L -> 0 //没有提醒
      it < TimeUtils.getTime() -> 1 //已经提醒
      else -> 2 //未提醒
    }
  }

  private val _deleteNoteEvent = MutableLiveData<Any>()
  val deleteNoteEvent: LiveData<Any>
    get() = _deleteNoteEvent

  private val _addImageEvent = MutableLiveData<Event<Any>>()
  val addImageEvent: LiveData<Event<Any>>
    get() = _addImageEvent

  private val _showSnackBarEvent = MutableLiveData<String>()
  val showSnackBarEvent: LiveData<String>
    get() = _showSnackBarEvent


  private val _saveNoteEvent = MutableLiveData<Int>()
  val saveNoteEvent: LiveData<Int>
    get() = _saveNoteEvent

  fun cancelEdit() {
    val listener = object : ConfermDialogFragment.ConfermListener {
      override fun onPositive() {
        _saveNoteEvent.postValue(NOCHANGE)
      }

      override fun onNegtive() {
      }
    }
    val title = "是否放弃编辑"
    ConfermDialogFragment(
      title,
      "",
      listener
    ).show(
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
    val lastModification = TimeUtils.getTime()
    val creation = _creation
    val notebookId = _notebookId
    val trashed = _trash
    val checkList = _checkList
    val topping = _topping.value!!
    val archived = _archived.value!!
    val sketch = _sketch
    val id = if (newNote) UUID.randomUUID().toString() else _noteId

    val alarm = _alarm.value!!
    if (alarm == 0L) {
      ReminderUtils.removeRimender(_creation)
    } else {
      ReminderUtils.updateRimender(_alarm.value!!, _creation, _noteId)
    }

    val coverImage: String

    //    处理保存的封面，uri无改变则不需要处理
    if (_imageUri.value != _initCoverUri) {
      if (_imageUri.value != Uri.EMPTY) {
        //      当前不为空，则删除之前，保存当前
        coverImage = repository.COVER.saveImageToExternal(_imageUri.value!!).toString()
        repository.COVER.deleteImageIfExist(_initCoverUri)
      } else {
        //        当前为空，则删除以前
        repository.COVER.deleteImageIfExist(_initCoverUri)
        coverImage = Uri.EMPTY.toString()
      }
    } else {
      coverImage = _initCoverUri.toString()
    }

    val note = Note(
      title,
      content,
      creation,
      lastModification,
      coverImage,
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
    val listener = object : EditReminderDialog.ConfermListener {
      override fun onPositive(reminder: Long) {
        _alarm.value = reminder
        if (reminder > TimeUtils.getTime()) {
          _showSnackBarEvent.value = "成功添加提醒"
        }
      }

      override fun onNegtive() {
        if (_alarm.value != 0L) {
          _showSnackBarEvent.value = "已删除提醒"
        }
        _alarm.value = 0L
      }
    }

    EditReminderDialog.newInstance(_creation, _alarm.value!!, listener)
      .show(fragment.parentFragmentManager, TAG)
  }

  fun backPressed() {
    if (newNote && title.value == "" && content.value == "") {
      _saveNoteEvent.value = NOCHANGE
      return
    }

    val listener = object : ConfermDialogFragment.ConfermListener {
      override fun onPositive() {
        if (newNote || _initSketch) _sketch = true
        saveNote()
      }

      override fun onNegtive() {
        _saveNoteEvent.value = NOCHANGE
      }
    }

    val title = if (newNote || _initSketch) "是否需要保存草稿" else "是否保存修改"
    ConfermDialogFragment(
      title,
      "",
      listener
    ).show(fragment.parentFragmentManager, javaClass.name)
  }

  fun start(fragment: AddEditNoteFragment, noteId: String, notebookId: Long?) {
    if (inited) return
    inited = true

    this.fragment = fragment
    this._noteId = noteId

    notebookPos.observe(fragment.viewLifecycleOwner, Observer {
      if (!spinnerInited) {
        //      spinner初始化后再赋值
        Thread { cyclicBarrier.await() }.start()
        spinnerInited = true
      } else {
        _notebookId = _notebooks.value?.get(it)?.id ?: _notebookId
        setColor()
      }
    })
    notebooks.observe(fragment.viewLifecycleOwner, Observer {
      Thread { cyclicBarrier.await() }.start()
    })

    if (noteId == "") {
      newNote = true
      _notebookId = notebookId!!
      Thread { cyclicBarrier.await() }.start()
      _hasImage.value = false
      _initCoverUri = Uri.EMPTY
      _creation = TimeUtils.getTime()
    } else {
      newNote = false

      val listener = object :
        Repository.LoadListener<Note> {
        override fun onSuccess(note: Note) {
          title.postValue(note.title)
          content.postValue(note.content)
          _alarm.postValue(note.alarm)
          _notebookId = note.notebookId
          _topping.postValue(note.topping)
          _archived.postValue(note.archived)
          cyclicBarrier.await()
          _creationText.postValue(note.createdDateString)
          _creationTextVisibility.postValue(true)
          _creation = note.creation
          _trash = note.trashed
          _checkList = note.checkList
          _initSketch = note.sketch
          if (note.coverImage.isNotEmpty()) {
            _imageUri.postValue(Uri.parse(note.coverImage))
            _hasImage.postValue(true)
            _initCoverUri = Uri.parse(note.coverImage)
          } else {
            _hasImage.postValue(false)
          }
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
      override fun onPositive() {
        _archived.postValue(!archived)
        fragment.view?.showSnackbar("已${if (archived) "取消归档" else "归档"}", Snackbar.LENGTH_SHORT)
      }

      override fun onNegtive() {
      }
    }
    val title = "是否${if (archived) "取消归档" else "归档"}"
    ConfermDialogFragment(
      title,
      "",
      listener
    ).show(
      fragment.parentFragmentManager,
      this.javaClass.name
    )
  }

  fun deleteNote() {
    val listener = object : ConfermDialogFragment.ConfermListener {
      override fun onPositive() {
        if (newNote) {
          _saveNoteEvent.postValue(NOCHANGE)
        } else {
          if (!_trash) {
            repository.NOTES.trashNoteById(_noteId)
          } else {
            repository.COVER.deleteImageIfExist(_initCoverUri)
            repository.NOTES.deleteNoteById(_noteId)
          }
          _deleteNoteEvent.postValue(Any())
        }
      }

      override fun onNegtive() {
      }
    }
    val title = "是否删除笔记"
    ConfermDialogFragment(
      title,
      "",
      listener
    ).show(
      fragment.parentFragmentManager,
      this.javaClass.name
    )
  }

  fun addImage() {
    _addImageEvent.value = Event(Any())
  }

  fun onImageClicked() {
    val listener = object : ConfermDialogFragment.ConfermListener {
      override fun onPositive() {
        _addImageEvent.value = Event(Any())
      }

      override fun onNegtive() {
        _hasImage.value = false
        _imageUri.value = Uri.EMPTY
      }
    }
    EditCoverDialog.newInstance(_imageUri.value ?: Uri.EMPTY, listener)
      .show(fragment.parentFragmentManager, TAG)
  }

  fun changeTopping() {
    val topping = this.topping.value!!
    val listener = object : ConfermDialogFragment.ConfermListener {
      override fun onPositive() {
        _topping.postValue(!topping)
        fragment.view?.showSnackbar("已${if (topping) "取消置顶" else "置顶"}", Snackbar.LENGTH_SHORT)
      }

      override fun onNegtive() {
      }
    }
    val title = "是否${if (topping) "取消置顶" else "置顶"}"
    ConfermDialogFragment(
      title,
      "",
      listener
    ).show(
      fragment.parentFragmentManager,
      this.javaClass.name
    )
  }

  fun handleResult(requestCode: Int, resultCode: Int, data: Intent?) {
    when (requestCode) {
      IntentUtils.OPEN_ALBUM -> {
        val uri = data?.data
        if (uri == null) {
          _hasImage.postValue(false)
          _imageUri.postValue(Uri.EMPTY)
        } else {
          _hasImage.postValue(true)
          _imageUri.postValue(uri)
        }
      }
    }
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