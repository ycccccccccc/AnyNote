package org.wangyichen.anynote.module.noteDetail

import android.graphics.Color
import android.view.View
import androidx.lifecycle.*
import com.google.android.material.snackbar.Snackbar
import org.wangyichen.anynote.module.AnyNoteApplication
import org.wangyichen.anynote.source.local.Repository
import org.wangyichen.anynote.utils.AppExecutors
import org.wangyichen.anynote.utils.ConfermDialogFragment
import org.wangyichen.anynote.utils.IntentUtils
import org.wangyichen.anynote.utils.ext.showSnackbar
import java.util.concurrent.CyclicBarrier

class NoteDetailViewModel(val noteId: Long) : ViewModel() {
  private val repository = Repository.getInstance(AnyNoteApplication.context)
  lateinit var fragment: NoteDetailFragment

  private val cyclicBarrier = CyclicBarrier(2) {
    onLoaded()
  }

  val _color = MutableLiveData<Int>()
  val color: LiveData<Int>
    get() = _color

  private val _notebooks = repository.NOTEBOOKS.getNotebooks()

  val _notebookName = MutableLiveData<String>()
  val notebookName: LiveData<String>
    get() = _notebookName

  private var _note = repository.NOTES.getNoteById(noteId)

  val title: LiveData<String> = Transformations.map(_note) {
    it.note.title
  }
  val content: LiveData<String> = Transformations.map(_note) { it.note.content }
  val arvhived: LiveData<Boolean> = Transformations.map(_note) { it.note.archived }
  val topping: LiveData<Boolean> = Transformations.map(_note) { it.note.topping }
  val creationText: LiveData<String> =
    Transformations.map(_note) { "创建于 ${it.note.createdDateString}" }
  val alarmText: LiveData<String> = Transformations.map(_note) {
    val note = it.note
    if (note.alarm == 0L) {
      "点击添加提醒"
    } else if (note.reminderFired) {
      "已提醒于 ${note.alarmString}"
    } else {
      "将提醒于 ${note.alarmString}"
    }
  }
  val imageVisibility: LiveData<Int> = Transformations.map(_note) {
    if (it.attachments == null || it.attachments.isEmpty()) {
      View.GONE
    } else {
      View.VISIBLE
    }
  }
  val imageUri: LiveData<String> = Transformations.map(_note) { it.attachments?.get(0)?.uri }
  val alarmShow: LiveData<Int> = Transformations.map(_note) {
    val note = it.note
    if (note.alarm == 0L) {
      0 //没有提醒
    } else if (note.reminderFired) {
      1 //已经提醒
    } else {
      2 //未提醒
    }
  }

  private val _deleteNoteEvent = MutableLiveData<Any>()
  val deleteNoteEvent: LiveData<Any>
    get() = _deleteNoteEvent

  private val _addEditNoteEvent = MutableLiveData<Long>()
  val addEditNoteEvent: LiveData<Long>
    get() = _addEditNoteEvent

  private val _showSnackBarEvent = MutableLiveData<String>()
  val showSnackBarEvent: LiveData<String>
    get() = _showSnackBarEvent

  fun start(fragment: NoteDetailFragment) {
    this.fragment = fragment
    _notebooks.observe(fragment.viewLifecycleOwner, Observer {
      Thread { cyclicBarrier.await()}.start()
    })
    _note.observe(fragment.viewLifecycleOwner, Observer {
      Thread { cyclicBarrier.await()}.start()
    })
  }

  fun changeArchived() {
    val archived = this.arvhived.value!!
    val listener = object : ConfermDialogFragment.ConfermListener {
      override fun onPostive() {
        repository.NOTES.changeNoteArchiveById(noteId, !archived)
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

  fun changeTopping() {
    val topping = this.topping.value!!
    val listener = object : ConfermDialogFragment.ConfermListener {
      override fun onPostive() {
        repository.NOTES.changeNoteToppingById(noteId, !topping)
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

  fun deleteNote() {
    val listener = object : ConfermDialogFragment.ConfermListener {
      override fun onPostive() {
        repository.NOTES.trashNoteById(noteId)
        _deleteNoteEvent.postValue(Any())
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

  private fun onLoaded() {
    for (notebook in _notebooks.value!!) {
      if (notebook.id == _note.value!!.note.notebookId) {
        _notebookName.postValue(notebook.name)
        _color.postValue(notebook.color)
      }
    }

  }

  companion object {
    fun getInstance(viewModelStoreOwner: ViewModelStoreOwner, noteId: Long) =
      ViewModelProvider(
        viewModelStoreOwner,
        NoteDetailViewModelFactory(noteId)
      )[NoteDetailViewModel::class.java]

  }

  class NoteDetailViewModelFactory(val noteid: Long) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>) =
      NoteDetailViewModel(noteid) as T
  }
}


