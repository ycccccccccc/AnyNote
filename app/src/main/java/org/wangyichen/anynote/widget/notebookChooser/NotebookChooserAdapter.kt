package org.wangyichen.anynote.widget.notebookChooser

import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import org.wangyichen.anynote.R
import org.wangyichen.anynote.module.AnyNoteApplication.Companion.context
import org.wangyichen.anynote.source.Entity.Notebook

class NotebookChooserAdapter(val notebooks: List<Notebook>,val listView: ListView) : BaseAdapter() {

  override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
    val view: View
    val holder: ViewHolder
    val notebook = getItem(position)
    if (convertView == null) {
      view = LayoutInflater.from(context).inflate(R.layout.item_notebook_chooser, parent, false)
      holder = ViewHolder(
        view.findViewById(R.id.notebook_color),
        view.findViewById(R.id.checkbox),
        view.findViewById(R.id.notebook_name)
      )
      view.tag = holder
    } else {
      view = convertView
      holder = view.tag as ViewHolder
    }
    holder.bg.setBackgroundColor(notebook.color)
    holder.checkBox.isChecked = listView.isItemChecked(position)
    holder.name.text = notebook.name
    return view
  }

  override fun getItem(position: Int) = notebooks[position]

  override fun getItemId(position: Int) = position.toLong()

  override fun getCount() = notebooks.size
  inner class ViewHolder(val bg: TextView, val checkBox: CheckBox, val name: TextView)
}