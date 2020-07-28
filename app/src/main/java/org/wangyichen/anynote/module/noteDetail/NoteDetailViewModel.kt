package org.wangyichen.anynote.module.noteDetail

import android.graphics.Color
import android.net.Uri
import android.view.View
import androidx.lifecycle.*
import com.google.android.material.snackbar.Snackbar
import org.wangyichen.anynote.Event
import org.wangyichen.anynote.module.AnyNoteApplication
import org.wangyichen.anynote.data.Entity.Note
import org.wangyichen.anynote.data.local.Repository
import org.wangyichen.anynote.utils.*
import org.wangyichen.anynote.utils.constant.AlarmState
import org.wangyichen.anynote.utils.ext.showSnackbar
import org.wangyichen.anynote.widget.ShareImageDialog
import org.wangyichen.anynote.widget.editReminderDialog.EditReminderDialog

class NoteDetailViewModel(val noteId: String) : ViewModel() {
  private val TAG = javaClass.name
  private val repository = Repository.getInstance(AnyNoteApplication.context)
  lateinit var fragment: NoteDetailFragment

  private val _notebooks = repository.NOTEBOOKS.getNotebooks()
  private var __note = repository.NOTES.getNoteById(noteId)

  //  避免删除数据库中的数据导致为null
  private var _note: LiveData<Note> = Transformations.map(__note) { it ?: Note() }

  val title: LiveData<String> = Transformations.map(_note) { it.title }
  val content: LiveData<String> = Transformations.map(_note) { it.content }
  val archived: LiveData<Boolean> = Transformations.map(_note) { it.archived }
  val topping: LiveData<Boolean> = Transformations.map(_note) { it.topping }
  val trashed: LiveData<Boolean> = Transformations.map(_note) { it.trashed }
  val creationText: LiveData<String> =
    Transformations.map(_note) { "创建于 ${it.createdDateString}" }
  val alarmText: LiveData<String> = Transformations.map(_note) { note ->
    when {
      note.alarm == 0L -> {
        "点击添加提醒"
      }
      note.reminderFired -> {
        "已提醒于 ${note.alarmString}"
      }
      else -> {
        "将提醒于 ${note.alarmString}"
      }
    }
  }
  val imageUri: LiveData<Uri> = Transformations.map(_note) { Uri.parse(it.coverImage) }
  val hasImage: LiveData<Boolean> = Transformations.map(_note) { it.coverImage.isNotEmpty() }
  val alarmShow: LiveData<Int> = Transformations.map(_note) { note ->
    when {
      note.alarm == 0L -> AlarmState.NOALARM  //没有提醒
      note.reminderFired -> AlarmState.FIRED  //已经提醒
      else -> AlarmState.NOTFIRED  //未提醒
    }
  }

  private val _deleteNoteEvent = MutableLiveData<Any>()
  val deleteNoteEvent: LiveData<Any>
    get() = _deleteNoteEvent

  private val _showCoverEvent = MutableLiveData<Event<Uri>>()
  val showCoverEvent: LiveData<Event<Uri>>
    get() = _showCoverEvent

  private val _shareNoteEvent = MutableLiveData<Event<Uri>>()
  val shareNoteEvent: LiveData<Event<Uri>>
    get() = _shareNoteEvent

  private val _addEditNoteEvent = MutableLiveData<String>()
  val addEditNoteEvent: LiveData<String>
    get() = _addEditNoteEvent

  private val _showSnackBarEvent = MutableLiveData<String>()
  val showSnackBarEvent: LiveData<String>
    get() = _showSnackBarEvent

  //  笔记与记事本变更后更新笔记本名称与颜色
  private val _changeEvent = MutableLiveData<Any>()
  val color: LiveData<Int> = Transformations.map(_changeEvent) {
    val notebookId = _note.value?.notebookId ?: -1L
    val notebooks = _notebooks.value ?: emptyList()
    var color = Color.WHITE
    for (notebook in notebooks) {
      if (notebook.id == notebookId) {
        color = notebook.color
      }
    }
    color
  }
  val notebookName: LiveData<String> = Transformations.map(_changeEvent) {
    val notebookId = _note.value?.notebookId ?: -1L
    val notebooks = _notebooks.value ?: emptyList()
    var name = ""
    for (notebook in notebooks) {
      if (notebook.id == notebookId) {
        name = notebook.name
      }
    }
    name
  }


  fun start(fragment: NoteDetailFragment) {
    this.fragment = fragment
    _notebooks.observe(fragment.viewLifecycleOwner, Observer {
      _changeEvent.value = Any()
    })
    _note.observe(fragment.viewLifecycleOwner, Observer {
      _changeEvent.value = Any()
    })
  }

  fun changeArchived() {
    val archived = this.archived.value!!
    val listener = object : ConfirmDialogFragment.ConfirmListener {
      override fun onPositive() {
        repository.NOTES.changeNoteArchiveById(noteId, !archived)
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

  fun restoreNote() {
    val listener = object : ConfirmDialogFragment.ConfirmListener {
      override fun onPositive() {
        repository.NOTES.untrashNoteById(noteId)
        _showSnackBarEvent.value = "已移出回收站"
      }

      override fun onNegative() {}
    }
    ConfirmDialogFragment("移出回收站", "", listener).show(fragment.parentFragmentManager, TAG)
  }

  fun alarmClicked() {
    val listener = object : EditReminderDialog.ConfirmListener {
      override fun onPositive(reminder: Long) {
        ReminderUtils.updateRimender(reminder, _note.value?.creation!!, noteId)
        repository.NOTES.updatealarmById(reminder, noteId)
        if (reminder > TimeUtils.getTime()) {
          _showSnackBarEvent.value = "成功添加提醒"
        }
      }

      override fun onNegtive() {
        //  有提醒时进行删除
        if (_note.value?.alarm != 0L) {
          repository.NOTES.updatealarmById(0L, noteId)
          ReminderUtils.removeRimender(_note.value?.creation!!)
          _showSnackBarEvent.value = "已删除提醒"
        }
      }
    }
    EditReminderDialog.newInstance(_note.value?.alarm!!, listener)
      .show(fragment.parentFragmentManager, TAG)
  }

  fun changeTopping() {
    val topping = this.topping.value!!
    val listener = object : ConfirmDialogFragment.ConfirmListener {
      override fun onPositive() {
        repository.NOTES.changeNoteToppingById(noteId, !topping)
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

  //  删除笔记，若已在回收站则彻底删除
  fun deleteNote() {
    val listener = object : ConfirmDialogFragment.ConfirmListener {
      override fun onPositive() {
        if (trashed.value != true) {
          repository.NOTES.trashNoteById(noteId)
        } else {
          repository.COVER.deleteImageIfExist(imageUri.value ?: Uri.EMPTY)
          repository.NOTES.deleteNoteById(noteId)
        }
        _deleteNoteEvent.postValue(Any())
      }

      override fun onNegative() {}
    }
    val title = if (!trashed.value!!) "删除笔记" else "彻底删除"
    val content = if (!trashed.value!!) "是否加入回收站" else "是否彻底删除笔记！"

    ConfirmDialogFragment(
      title,
      content,
      listener
    ).show(
      fragment.parentFragmentManager,
      this.javaClass.name
    )
  }

  fun editNote() {
    _addEditNoteEvent.value = noteId
  }

  fun handleResult(requestCode: Int, resultCode: Int) {
    when (requestCode) {
      IntentUtils.REQUEST_CODE_ADD_EDIT_NOTE_ACTIVITY -> when (resultCode) {
        IntentUtils.ADD_EDIT_RESULT_OK -> _showSnackBarEvent.value = "成功编辑笔记"
        IntentUtils.DELETE_RESULT_OK -> _deleteNoteEvent.value = Any()
      }
    }
  }

  fun onImageClicked() {
    _showCoverEvent.value = Event(imageUri.value ?: Uri.EMPTY)
  }

  fun shareImage(view: View) {
    ViewDrawUtils.draw(view) { bitmap ->
      val uri = repository.COVER.saveImageToCache(bitmap)
      val listener = object : ConfirmDialogFragment.ConfirmListener {
        override fun onPositive() {
          _shareNoteEvent.postValue(Event(uri))
        }

        override fun onNegative() {}
      }
      ShareImageDialog.getInstance(bitmap, listener).show(fragment.parentFragmentManager, TAG)
    }

  }

  fun saveImage(view: View) {
    ViewDrawUtils.draw(view) { bitmap ->
      repository.COVER.saveImageToDCIM(bitmap, title.value!!)
      _showSnackBarEvent.postValue("图片已保存到本地")
    }
  }

  companion object {
    fun getInstance(viewModelStoreOwner: ViewModelStoreOwner, noteId: String) =
      ViewModelProvider(
        viewModelStoreOwner,
        NoteDetailViewModelFactory(noteId)
      )[NoteDetailViewModel::class.java]
  }

  class NoteDetailViewModelFactory(val noteid: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>) =
      NoteDetailViewModel(noteid) as T
  }
}


