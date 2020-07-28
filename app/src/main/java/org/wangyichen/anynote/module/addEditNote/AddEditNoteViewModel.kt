package org.wangyichen.anynote.module.addEditNote

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import org.wangyichen.anynote.Event
import org.wangyichen.anynote.data.Entity.Note
import org.wangyichen.anynote.data.local.Repository
import org.wangyichen.anynote.module.AnyNoteApplication
import org.wangyichen.anynote.utils.ConfirmDialogFragment
import org.wangyichen.anynote.utils.IntentUtils
import org.wangyichen.anynote.utils.ReminderUtils
import org.wangyichen.anynote.utils.TimeUtils
import org.wangyichen.anynote.utils.constant.AlarmState
import org.wangyichen.anynote.utils.ext.showSnackbar
import org.wangyichen.anynote.widget.EditCoverDialog
import org.wangyichen.anynote.widget.editReminderDialog.EditReminderDialog
import org.wangyichen.anynote.widget.notebookChooser.NotebookChooserDialog
import java.util.*
import java.util.concurrent.CyclicBarrier

class AddEditNoteViewModel : ViewModel() {
  private val TAG = this.javaClass.name
  private val repository = Repository.getInstance(AnyNoteApplication.context)
  lateinit var fragment: AddEditNoteFragment

  private var newNote = true
  private var _initCoverUri = Uri.EMPTY
  private var _initSketch = false

  //  需要notebooks和note加载完成再初始化notebookName
  private val cyclicBarrier = CyclicBarrier(2) {
    setNotebookName()
  }

  private var _noteId: String = ""
  private var _notebookId = 0L
  private var _sketch = false
  private var _creation = 0L
  private var _trash = false
  private var _checkList = false

  private val _notebooks = repository.NOTEBOOKS.getNotebooks()
  val title = MutableLiveData<String>().apply { value = "" }
  val content = MutableLiveData<String>().apply { value = "" }

  private var _color = MutableLiveData<Int>()
  val color: LiveData<Int>
    get() = _color

  val _notebookName = MutableLiveData<String>()
  val notebookName: LiveData<String>
    get() = _notebookName

  private val _archived = MutableLiveData<Boolean>().apply { value = false }
  val archived: LiveData<Boolean>
    get() = _archived

  private val _topping = MutableLiveData<Boolean>().apply { value = false }
  val topping: LiveData<Boolean>
    get() = _topping

  private val _creationTextVisibility = MutableLiveData<Boolean>().apply { value = false }
  val creationTextVisibility: LiveData<Boolean>
    get() = _creationTextVisibility

  private val _creationTime = MutableLiveData<String>()
  val creationText: LiveData<String> = Transformations.map(_creationTime) {
    "创建时间：$it"
  }

  private val _alarm = MutableLiveData<Long>().apply { value = 0L }
  val alarmText: LiveData<String> = Transformations.map(_alarm) {
    when {
      it == 0L -> "点击添加提醒"
      it < TimeUtils.getTime() -> "已提醒于 ${TimeUtils.time2String(it)}"
      else -> "将提醒于 ${TimeUtils.time2String(it)}"
    }
  }

  private val _imageUri = MutableLiveData<Uri>().apply { value = Uri.EMPTY }
  val imageUri: LiveData<Uri>
    get() = _imageUri

  private val _hasImage = MutableLiveData<Boolean>()
  val hasImage: LiveData<Boolean>
    get() = _hasImage

  val alarmState: LiveData<Int> = Transformations.map(_alarm) {
    when {
      it == 0L -> AlarmState.NOALARM //没有提醒
      it < TimeUtils.getTime() -> AlarmState.FIRED //已经提醒
      else -> AlarmState.NOTFIRED //未提醒
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
    val listener = object : ConfirmDialogFragment.ConfirmListener {
      override fun onPositive() {
        _saveNoteEvent.postValue(NOCHANGE)
      }

      override fun onNegative() {}
    }
    val title = "是否放弃编辑"
    ConfirmDialogFragment(
      title,
      "",
      listener
    ).show(
      fragment.parentFragmentManager,
      this.javaClass.name
    )
  }

  fun saveNote() {
    //  不是保存草稿时，进行判断
    if (!_sketch) {
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

    //  处理保存的封面，uri无改变则不需要处理
    if (_imageUri.value != _initCoverUri) {
      if (_imageUri.value != Uri.EMPTY) {
        //  当前不为空，则删除之前，保存当前
        coverImage = repository.COVER.saveImageToExternal(_imageUri.value!!).toString()
        repository.COVER.deleteImageIfExist(_initCoverUri)
      } else {
        //  当前为空，则删除以前
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
    val listener = object : EditReminderDialog.ConfirmListener {
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

    EditReminderDialog.newInstance(_alarm.value!!, listener)
      .show(fragment.parentFragmentManager, TAG)
  }

  //  返回事件
  fun backPressed() {
    //  新建笔记且未更改，直接返回
    if (newNote && title.value == "" && content.value == "") {
      _saveNoteEvent.value = NOCHANGE
      return
    }

    //  若是新笔记或者草稿，提醒保存草稿。否则提醒保存修改
    val listener = object : ConfirmDialogFragment.ConfirmListener {
      override fun onPositive() {
        if (newNote || _initSketch) _sketch = true
        saveNote()
      }

      override fun onNegative() {
        _saveNoteEvent.value = NOCHANGE
      }
    }

    val title = if (newNote || _initSketch) "是否需要保存草稿" else "是否保存修改"
    ConfirmDialogFragment(
      title,
      "",
      listener
    ).show(fragment.parentFragmentManager, javaClass.name)
  }

  fun start(fragment: AddEditNoteFragment, noteId: String, notebookId: Long?) {

    this.fragment = fragment
    this._noteId = noteId

    _notebooks.observe(fragment.viewLifecycleOwner, Observer {
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
          _creationTime.postValue(note.createdDateString)
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
          Log.e(TAG,e.toString())
        }
      }
      repository.NOTES.getNoteById(noteId, listener)
    }
  }

  fun changeArchived() {
    val archived = this.archived.value!!
    val listener = object : ConfirmDialogFragment.ConfirmListener {
      override fun onPositive() {
        _archived.postValue(!archived)
        fragment.view?.showSnackbar("已${if (archived) "取消归档" else "归档"}", Snackbar.LENGTH_SHORT)
      }

      override fun onNegative() {}
    }
    val title = "是否${if (archived) "取消归档" else "归档"}"
    ConfirmDialogFragment(
      title,
      "",
      listener
    ).show(
      fragment.parentFragmentManager,
      this.javaClass.name
    )
  }

  fun deleteNote() {
    val listener = object : ConfirmDialogFragment.ConfirmListener {
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

      override fun onNegative() {}
    }
    val title = "是否删除笔记"
    ConfirmDialogFragment(
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
    val listener = object : ConfirmDialogFragment.ConfirmListener {
      override fun onPositive() {
        _addImageEvent.value = Event(Any())
      }

      //  删除
      override fun onNegative() {
        _hasImage.value = false
        _imageUri.value = Uri.EMPTY
      }
    }
    EditCoverDialog.newInstance(_imageUri.value ?: Uri.EMPTY, listener)
      .show(fragment.parentFragmentManager, TAG)
  }

  fun onNotebookClicked() {
    val listener = object : NotebookChooserDialog.ConfirmListener {
      override fun onPositive(notebookId: Long) {
        _notebookId = notebookId
        setNotebookName()
      }
    }
    NotebookChooserDialog.show(
      1,
      _notebooks.value!!,
      listener,
      fragment.parentFragmentManager,
      TAG,
      false
    )
  }

  fun changeTopping() {
    val topping = this.topping.value!!
    val listener = object : ConfirmDialogFragment.ConfirmListener {
      override fun onPositive() {
        _topping.postValue(!topping)
        fragment.view?.showSnackbar("已${if (topping) "取消置顶" else "置顶"}", Snackbar.LENGTH_SHORT)
      }

      override fun onNegative() {}
    }
    val title = "是否${if (topping) "取消置顶" else "置顶"}"
    ConfirmDialogFragment(
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
          //  未选择图片则不更改原有图片
          if (_imageUri.value != Uri.EMPTY) {
            return
          }
          _hasImage.postValue(false)
          _imageUri.postValue(Uri.EMPTY)
        } else {
          _hasImage.postValue(true)
          _imageUri.postValue(uri)
        }
      }
    }
  }

  private fun setNotebookName() {
    var name = "默认笔记本"
    var color = Color.WHITE
    for (notebook in _notebooks.value!!) {
      if (notebook.id == _notebookId) {
        name = notebook.name
        color = notebook.color
      }
    }
    _notebookName.postValue(name)
    _color.postValue(color)
  }

  companion object {
    const val NOCHANGE = 0
    const val SAVE = 1
    const val SKETCH = 2


    fun newInstance(owner: ViewModelStoreOwner) =
      ViewModelProvider(owner).get(AddEditNoteViewModel::class.java)
  }
}