package org.wangyichen.anynote.module.notes

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.wangyichen.anynote.R
import org.wangyichen.anynote.data.Entity.Note
import org.wangyichen.anynote.data.Entity.Notebook
import org.wangyichen.anynote.utils.constant.SortType

class NotesAdapter(
  val viewModel: NotesViewModel
) :
  RecyclerView.Adapter<NotesAdapter.MainNoteViewHolder>() {

  internal var notes = ArrayList<NoteWrapper>()
  private var notebooks = ArrayList<Notebook>()
  private var selectedNotes = HashSet<Note>()
  var isActionMode = false
  val listener: NotesItemActionListener = object : NotesItemActionListener {
    override fun onNoteClicked(note: Note) {
      viewModel.openNoteDetails(note)
    }

    override fun onToppingClicked(note: Note) {
      viewModel.unToppingNote(note)
    }
  }

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
    val checkBox: CheckBox = view.findViewById(R.id.checkbox)
  }

  private var sort: Long = SortType.CREATION

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainNoteViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
    return MainNoteViewHolder(view)
  }

  override fun getItemCount() = notes.size

  override fun onBindViewHolder(holder: MainNoteViewHolder, position: Int) {
    val noteWraper = notes[position]
    val note = noteWraper.note

    holder.view.setOnClickListener {
//    action mode 与 常规逻辑
      if (isActionMode) {
        if (noteWraper.checked) {
          noteWraper.checked = false
          holder.checkBox.isChecked = false
          selectedNotes.remove(note)
        } else {
          noteWraper.checked = true
          holder.checkBox.isChecked = true
          selectedNotes.add(note)
        }
        viewModel.onSelectedNotesChanged(selectedNotes)
      } else {
        listener.onNoteClicked(note)
      }
    }
//    长按触发action mode
    holder.view.setOnLongClickListener {
      if (!isActionMode) {
        isActionMode = true

//        初始化 只有当前选中
        for (note in notes) note.checked = false
        selectedNotes.clear()
        selectedNotes.add(note)
        noteWraper.checked = true

        viewModel.startActionMode()
        viewModel.onSelectedNotesChanged(selectedNotes)
        notifyDataSetChanged()
        true
      } else {
        false
      }
    }

//    选择框根据action mode变化
    if (isActionMode) {
      holder.checkBox.visibility = View.VISIBLE
      holder.checkBox.isChecked = noteWraper.checked
    } else {
      holder.checkBox.visibility = View.GONE
    }

//    置顶标志
    holder.iv_topping.apply {
      visibility = if (!note.topping) View.GONE else View.VISIBLE
      setOnClickListener {
        listener.onToppingClicked(note)
      }
      isClickable = !isActionMode
    }

//    封面
    holder.iv_image.apply {
      val uri = note.coverImage
      visibility = if (uri.isNotEmpty()) {
        Glide.with(this).load(Uri.parse(uri)).centerCrop().into(this)
        View.VISIBLE
      } else {
        View.GONE
      }
    }

    holder.tv_title.text = note.title
    holder.tv_content.text = note.content

//    创建时间/修改时间  根据排序方式变化
    holder.tv_time.text = when (sort) {
      SortType.CREATION -> "创建时间：${note.createdDateString}"
      SortType.LASTMODIFICATION -> "修改时间：${note.modificatedDateString}"
      else -> "创建时间：${note.createdDateString}"
    }


//    提醒文本显示
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

//    笔记本对应颜色
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

  fun setNotes(notes: List<Note>) {
    this.notes.clear()
    this.notes.addAll(notes.map { NoteWrapper(false, it) })
    notifyDataSetChanged()
  }

  fun setSort(sort: Long) {
    this.sort = sort
  }

  fun setNotebooks(notebooks: List<Notebook>) {
    this.notebooks.clear()
    this.notebooks.addAll(notebooks)
    notifyDataSetChanged()
  }

  fun closeActionMode() {
    isActionMode = false
    notifyDataSetChanged()
  }

  fun selectAll() {
    for (note in notes) {
      note.checked = true
      selectedNotes.add(note.note)
    }
    viewModel.onSelectedNotesChanged(selectedNotes)
    notifyDataSetChanged()
  }

  class NoteWrapper(var checked: Boolean, val note: Note)
}