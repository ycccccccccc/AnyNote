package org.wangyichen.anynote.widget.notebookChooser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.dialog_notebook_chooser.*
import org.wangyichen.anynote.R
import org.wangyichen.anynote.data.Entity.Notebook
import org.wangyichen.anynote.utils.ConfirmDialogFragment
import org.wangyichen.anynote.utils.SystemUtils
import org.wangyichen.anynote.utils.ext.showSnackbar

/*
* needConfirm : 修改笔记本时是否需要提醒
*/
class NotebookChooserDialog(
  private val count: Int,
  val notebooks: List<Notebook>,
  val listener: ConfirmListener,
  private val needConfirm: Boolean
) :
  DialogFragment() {
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.dialog_notebook_chooser, container, false)
  }

  private lateinit var adapter: NotebookChooserAdapter
  private var selectedIdx = -1

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setupListView()
    setupButton()
  }

  private fun setupButton() {
    positive.setOnClickListener {
      if (selectedIdx == -1) {
        view?.showSnackbar("未选择笔记本", Snackbar.LENGTH_SHORT)
        return@setOnClickListener
      }
      val name = notebooks[selectedIdx].name
      val id = notebooks[selectedIdx].id
      if (needConfirm) {
        val confirmlistener = object : ConfirmDialogFragment.ConfirmListener {
          override fun onPositive() {
            listener.onPositive(id!!)
            dismiss()
          }

          override fun onNegative() {}
        }
        val title = "移动笔记"
        val content = "确认将 $count 条笔记移动至 $name 笔记本？"
        ConfirmDialogFragment.show(
          title,
          content,
          confirmlistener,
          parentFragmentManager,
          javaClass.name
        )
      } else {
        listener.onPositive(id!!)
        dismiss()
      }
    }
    negative.setOnClickListener {
      dismiss()
    }
  }

  override fun onResume() {
    super.onResume()
    val width =
      SystemUtils.getWidthByPercentage(getString(R.string.notebook_chooser_dialog_width).toFloat())
    val height =
      SystemUtils.getHeightByPercentage(getString(R.string.notebook_chooser_dialog_height).toFloat())
    dialog?.window?.setLayout(width, height)
  }

  private fun setupListView() {
    adapter = NotebookChooserAdapter(notebooks, list_item)
    list_item.adapter = adapter
    list_item.setOnItemClickListener { parent, view, position, id ->
      adapter.notifyDataSetChanged()
      selectedIdx = position
    }
  }


  companion object {
    fun show(
      count: Int,
      notebooks: List<Notebook>,
      listener: ConfirmListener,
      manager: FragmentManager,
      tag: String,
      needConfirm: Boolean = true
    ) {
      NotebookChooserDialog(
        count,
        notebooks,
        listener,
        needConfirm
      ).show(manager, tag)
    }
  }

  interface ConfirmListener {
    fun onPositive(notebookId: Long)
  }
}