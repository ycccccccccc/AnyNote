package org.wangyichen.anynote.widget.addEditNotebookDialog

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.*
import org.wangyichen.anynote.Event
import org.wangyichen.anynote.module.AnyNoteApplication
import org.wangyichen.anynote.source.local.Repository
import org.wangyichen.anynote.widget.colorChooser.ColorChooserDialog
import org.wangyichen.anynote.R
import org.wangyichen.anynote.module.AnyNoteApplication.Companion.context
import org.wangyichen.anynote.module.DEFAULT_NOTEBOOK_ID
import org.wangyichen.anynote.source.Entity.Notebook
import org.wangyichen.anynote.source.Entity.Tag
import org.wangyichen.anynote.utils.ConfermDialogFragment
import java.lang.Exception

class AddEditNotebookViewModel : ViewModel() {
  private val TAG = this.javaClass.name
  private val repository = Repository.getInstance(AnyNoteApplication.context)
  private var newNotebook = true
  private var notebookId = -1L
  private val colors = context.resources.getIntArray(R.array.preset_colors)


  lateinit var owner: ViewModelStoreOwner
  lateinit var fragment: AddEditNotebookDialogFragment

  val name = MutableLiveData<String>()
  val description = MutableLiveData<String>()

  private val _negativeText = MutableLiveData<String>()
  val negativeText: LiveData<String>
    get() = _negativeText

  private val _color = MutableLiveData<Int>()
  val color: LiveData<Int>
    get() = _color

  private val _positiveText = MutableLiveData<String>()
  val positiveText: LiveData<String>
    get() = _positiveText

  private val _closeEvent = MutableLiveData<Event<Any>>()
  val closeEvent: LiveData<Event<Any>>
    get() = _closeEvent

  private val _showSnackBarEvent = MutableLiveData<Event<String>>()
  val showSnackBarEvent: LiveData<Event<String>>
    get() = _showSnackBarEvent


  fun start(notebookId: Long, owner: ViewModelStoreOwner, fragment: AddEditNotebookDialogFragment) {
    if (notebookId == -1L) {
      newNotebook = true
      _negativeText.value = "取消"
      _positiveText.value = "创建"
      _color.value = colors.random()
      name.value = ""
      description.value = ""
    } else {
      _negativeText.value = "删除"
      _positiveText.value = "保存"
      newNotebook = false
    }
    this.notebookId = notebookId
    this.owner = owner
    this.fragment = fragment
    load()
  }


  fun load() {
    val listener = object : Repository.LoadListener {
      override fun onSuccess(item: Any) {
        val notebook = item as Notebook
        _color.postValue(notebook.color)
        name.postValue(notebook.name)
        description.postValue(notebook.description)
      }

      override fun onError(e: Exception) {
        Log.e(TAG, e.toString())
      }

    }
    repository.NOTEBOOKS.getNoLiveNotebookById(notebookId, listener)
  }

  fun chooseColor() {
    val listener = object : ColorChooserDialog.SaveColorListener {
      override fun onSave(color: Int) {
        _color.value = color
      }
    }
    ColorChooserDialog.getInstance(owner, _color.value!!, listener)
      .show(fragment.parentFragmentManager, TAG)
  }

  fun onNegativeClick() {
    if (!newNotebook) {
      val listener = object : ConfermDialogFragment.ConfermListener {
        override fun onPostive() {
          repository.NOTEBOOKS.deleteNotebook(notebookId)
          _closeEvent.postValue(Event(Any()))
        }

        override fun onNegtive() {
        }
      }
      ConfermDialogFragment(
        "删除笔记本",
        "是否删除笔记本，笔记将放入回收站，此操作不可逆！",
        listener
      ).show(fragment.parentFragmentManager, TAG)
    } else {
      _closeEvent.value = Event(Any())
    }
  }

  fun onPositiveClick() {
    val name = this.name.value!!
    if (name.isEmpty()) {
      _showSnackBarEvent.value = Event("笔记本内容不能为空")
      return
    }
    val color = this._color.value!!
    val description = this.description.value!!
    val notebookId = if (newNotebook) null else this.notebookId!!

    val notebook = Notebook(name = name, color = color, description = description, id = notebookId)
    repository.NOTEBOOKS.saveNotebook(notebook)
    _closeEvent.value = Event(Any())
  }

  companion object {
    const val EDIT = 0
    const val COLOR_LIST = 1
    const val SUSTOM_COLOR = 2

    fun getInstance(viewModelStoreOwner: ViewModelStoreOwner) =
      ViewModelProvider(viewModelStoreOwner).get(AddEditNotebookViewModel::class.java)
  }
}