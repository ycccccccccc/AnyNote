package org.wangyichen.anynote.widget.notebookChooser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.dialog_notebook_chooser.*
import org.wangyichen.anynote.R
import org.wangyichen.anynote.source.Entity.Notebook
import org.wangyichen.anynote.utils.ConfermDialogFragment
import org.wangyichen.anynote.utils.SystemUtils


class NotebookChooserDialog(
  val count: Int,
  val notebooks: List<Notebook>,
  val listener: ConfermListener
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
      val name = notebooks[selectedIdx].name
      val id = notebooks[selectedIdx].id
      val confermlistener = object : ConfermDialogFragment.ConfermListener {
        override fun onPositive() {
          listener.onPositive(id!!)
          dismiss()
        }

        override fun onNegtive() {}
      }
      val title = "移动笔记"
      val content = "确认将 $count 条笔记移动至 $name 笔记本？"
      ConfermDialogFragment.show(title,content,confermlistener,parentFragmentManager,javaClass.name)
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
      listener: ConfermListener,
      manager: FragmentManager,
      tag: String
    ) {
      NotebookChooserDialog(
        count,
        notebooks,
        listener
      ).show(manager, tag)
    }
  }

  interface ConfermListener {
    fun onPositive(notebookId: Long)
  }
}