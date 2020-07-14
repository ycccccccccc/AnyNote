package org.wangyichen.anynote.module.notes

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.wangyichen.anynote.R
import org.wangyichen.anynote.source.Entity.NoteWithOthers
import org.wangyichen.anynote.source.Entity.Notebook
import org.wangyichen.anynote.utils.constant.SortType

class NotesAdapter(
  val notes: List<NoteWithOthers>,
  val notebooks: List<Notebook>,
  val listener: NotesItemActionListener
) :
  RecyclerView.Adapter<NotesAdapter.MainNoteViewHolder>() {

  inner class MainNoteViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    val iv_alarm: ImageView = view.findViewById(R.id.iv_alarm)
    val iv_alarmed: ImageView = view.findViewById(R.id.iv_alarmed)
    val iv_image: ImageView = view.findViewById(R.id.iv_image)
    val iv_topping: ImageView = view.findViewById(R.id.iv_topping)
    val tv_title: TextView = view.findViewById(R.id.tv_title)
    val tv_content: TextView = view.findViewById(R.id.tv_content)
    val tv_time: TextView = view.findViewById(R.id.tv_time)
    val tv_alarm_time: TextView = view.findViewById(R.id.tv_alarm_time)
    val note_color: TextView = view.findViewById(R.id.note_color)
  }

  var sort: Long = SortType.CREATION
    set(value) {
      field = value
    }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainNoteViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
    val vh = MainNoteViewHolder(view)
    return vh
  }

  override fun getItemCount() = notes.size

  override fun onBindViewHolder(holder: MainNoteViewHolder, position: Int) {
    val notesWithOthers = notes[position]
    val note = notesWithOthers.note

    holder.view.setOnClickListener {
      listener.onNoteClicked(note)
    }
    holder.iv_topping.apply {
      visibility = if (!note.topping) View.GONE else View.VISIBLE
      setOnClickListener {
        visibility = if (listener.onToppingClicked(note)) View.GONE else View.VISIBLE
      }
    }
    holder.iv_image.apply {

    }
    holder.tv_title.text = note.title
    holder.tv_content.text = note.content
    holder.tv_time.text = when (sort) {
      SortType.CREATION -> "创建时间：${note.createdDateString}"
      SortType.LASTMODIFICATION -> "修改时间：${note.modificatedDateString}"
      else -> "创建时间：${note.createdDateString}"
    }
    if (note.alarm == 0L) {
      holder.tv_alarm_time.text = "未设置提醒时间"
      holder.tv_alarm_time.visibility = View.VISIBLE
      holder.iv_alarm.visibility = View.GONE
      holder.iv_alarmed.visibility = View.GONE
    } else if (!note.reminderFired) {
      holder.tv_alarm_time.text = "提醒时间：${note.alarmString}"
      holder.tv_alarm_time.visibility = View.VISIBLE
      holder.iv_alarm.visibility = View.VISIBLE
      holder.iv_alarmed.visibility = View.GONE
    } else {
      holder.tv_alarm_time.text = "已经触发提醒于：${note.alarmString}"
      holder.tv_alarm_time.visibility = View.VISIBLE
      holder.iv_alarm.visibility = View.GONE
      holder.iv_alarmed.visibility = View.VISIBLE
    }

    val bg = holder.note_color.background as GradientDrawable
    var notebook: Notebook? = null
    for (nb in notebooks) {
      if (nb.id == note.notebookId) {
        notebook = nb
        break
      }
    }
    bg.setColor(notebook?.color ?: Color.TRANSPARENT)
  }
}