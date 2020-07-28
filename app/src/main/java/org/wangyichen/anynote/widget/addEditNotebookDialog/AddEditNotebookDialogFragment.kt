package org.wangyichen.anynote.widget.addEditNotebookDialog

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelStoreOwner
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.dialog_add_edit_notebook.*
import org.wangyichen.anynote.R
import org.wangyichen.anynote.databinding.DialogAddEditNotebookBinding
import org.wangyichen.anynote.utils.SystemUtils
import org.wangyichen.anynote.utils.ext.showSnackbar

class AddEditNotebookDialogFragment(val listener: DeleteListener?) : DialogFragment() {
  lateinit var binding: DialogAddEditNotebookBinding
  lateinit var owner: ViewModelStoreOwner


  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = DialogAddEditNotebookBinding.inflate(inflater, container, false).apply {
      viewmodel = AddEditNotebookViewModel.getInstance(owner)
    }
    binding.lifecycleOwner = this
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    observeLiveData()
  }

  fun observeLiveData() {
    binding.viewmodel?.run {
      color.observe(viewLifecycleOwner, Observer {
        iv_choose_color.drawable.setColorFilter(it, PorterDuff.Mode.MULTIPLY)
      })
      closeEvent.observe(viewLifecycleOwner, Observer {
        val content = it.getContent()
        if (content != null) {
          this@AddEditNotebookDialogFragment.dismiss()
        }
      })
      showSnackBarEvent.observe(viewLifecycleOwner, Observer {
        val content = it.getContent()
        if (content != null) {
          view?.showSnackbar(content, Snackbar.LENGTH_SHORT)
        }
      })
      deleteEvent.observe(viewLifecycleOwner, Observer {
        val content = it.getContent()
        if (content != null && listener != null) {
          listener.onDelete()
        }
      })
    }
  }

  override fun onResume() {
    super.onResume()
    val width =
      SystemUtils.getWidthByPercentage(getString(R.string.add_edit_notebook_dialog_width).toFloat())
    val height =
      SystemUtils.getHeightByPercentage(getString(R.string.add_edit_notebook_dialog_height).toFloat())
    dialog?.window?.setLayout(width, height)
    binding.viewmodel?.start(arguments?.getLong(NOTEBOOKID)!!, owner, this)
  }

  companion object {
    const val NOTEBOOKID = "notebookid"
    fun getInstance(notebookId: Long, owner: ViewModelStoreOwner,listener:DeleteListener? = null) = AddEditNotebookDialogFragment(listener)
      .apply {
        this.owner = owner
        arguments = Bundle().apply {
          putLong(NOTEBOOKID, notebookId)
        }
      }
  }
  interface DeleteListener {
    fun onDelete()
  }
}