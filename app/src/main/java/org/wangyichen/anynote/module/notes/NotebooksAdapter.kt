package org.wangyichen.anynote.module.notes

import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import org.wangyichen.anynote.R
import org.wangyichen.anynote.module.AnyNoteApplication.Companion.context
import org.wangyichen.anynote.source.Entity.Notebook
import org.wangyichen.anynote.utils.constant.NotebookIdExt

class NotebooksAdapter(val notebooks: List<Notebook>, val viewModel: NotesViewModel,val drawerLayout: DrawerLayout) :
  RecyclerView.Adapter<NotebooksAdapter.NotebooksViewHolder>(), NotebooksItemActionListener {
  private val CUSTOM = -1
  private val ALL = 0
  private val ARCHIVED = 1
  private val TRASH = 2
  private val SKETCH = 3
  private val NEW = 4


  class NotebooksViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    val iv_icon: ImageView = view.findViewById(R.id.notebook_icon)
    val tv_name: TextView = view.findViewById(R.id.notebook_name)
    val tv_count: TextView = view.findViewById(R.id.notebook_count)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotebooksViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notebook, parent, false)
    val vh = NotebooksViewHolder(view)
    return vh
  }

  /*
  * 所有
  * 已归档
  * 回收站
  * 草稿箱
  * notebooks
  * 新增*/
  override fun getItemCount() = notebooks.size + 5

  override fun onBindViewHolder(holder: NotebooksViewHolder, position: Int) {
    when (position) {
//      所有
      0 -> {
        holder.iv_icon.setImageResource(R.drawable.notebook_all_note_white)
        holder.iv_icon.drawable.setColorFilter(context.resources.getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY)
        holder.tv_name.text = context.getString(R.string.notebook_name_all_note)
        holder.tv_count.visibility = View.GONE
        holder.view.setOnClickListener { onClick(ALL, Notebook()) }
      }
//      已归档
      1 -> {
        holder.iv_icon.setImageResource(R.drawable.notebook_archived_white)
        holder.iv_icon.drawable.setColorFilter(context.resources.getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY)
        holder.tv_name.text = context.getString(R.string.notebook_name_archived)
        holder.tv_count.visibility = View.GONE
        holder.view.setOnClickListener { onClick(ARCHIVED, Notebook()) }
      }
//      回收站
      2 -> {
        holder.iv_icon.setImageResource(R.drawable.notebook_delete_white)
        holder.iv_icon.drawable.setColorFilter(context.resources.getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY)
        holder.tv_name.text = context.getString(R.string.notebook_name_trashed)
        holder.tv_count.visibility = View.GONE
        holder.view.setOnClickListener { onClick(TRASH, Notebook()) }
      }
//      草稿箱
      3 -> {
        holder.iv_icon.setImageResource(R.drawable.notebook_sketch_white)
        holder.iv_icon.drawable.setColorFilter(context.resources.getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY)
        holder.tv_name.text = context.getString(R.string.notebook_name_sketch)
        holder.tv_count.visibility = View.GONE
        holder.view.setOnClickListener { onClick(SKETCH, Notebook()) }
      }
//      新建笔记本
      notebooks.size + 4 -> {
        holder.iv_icon.setImageResource(R.drawable.notebook_add_white)
        holder.iv_icon.drawable.setColorFilter(context.resources.getColor(R.color.colorAccent), PorterDuff.Mode.MULTIPLY)
        holder.tv_name.text = "新建笔记本"
        holder.tv_count.visibility = View.GONE
        holder.view.setOnClickListener { onClick(NEW, Notebook()) }
      }
//      自定义笔记本s
      else -> {
        val notebook = notebooks[position-4]
        holder.iv_icon.setImageResource(R.drawable.cycle)
        holder.iv_icon.drawable.setColorFilter(notebook.color, PorterDuff.Mode.MULTIPLY)
        holder.iv_icon.setBackgroundResource(R.drawable.outline)
        holder.tv_name.text = notebook.name
        holder.tv_count.visibility = View.GONE
        holder.view.setOnClickListener { onClick(CUSTOM, notebook) }
        holder.view.setOnLongClickListener { onLongClicked(CUSTOM, notebook) }
      }
    }
  }

  override fun onClick(case: Int, notebook: Notebook) {
    when (case) {
      CUSTOM -> {
        viewModel.openNotebook(notebook.id!!, notebook.name)
      }
      ALL -> {
        viewModel.openNotebook(
          NotebookIdExt.ALLNOTES,
          context.getString(R.string.notebook_name_all_note)
        )
      }
      ARCHIVED -> {
        viewModel.openNotebook(
          NotebookIdExt.ARCHIVED,
          context.getString(R.string.notebook_name_archived)
        )
      }
      TRASH -> {
        viewModel.openNotebook(
          NotebookIdExt.TRASH,
          context.getString(R.string.notebook_name_trashed)
        )
      }
      SKETCH -> {
        viewModel.openNotebook(
          NotebookIdExt.SKETCH,
          context.getString(R.string.notebook_name_sketch)
        )
      }
      NEW -> {
        viewModel.addNewNotebook()
      }
      else -> {
      }
    }
    drawerLayout.closeDrawers()
  }

  override fun onLongClicked(case: Int, notebook: Notebook): Boolean =
    when (case) {
      CUSTOM -> {
        viewModel.openNotebook(notebook.id!!, notebook.name)
        true
      }
      else -> {
        false
      }
    }
}