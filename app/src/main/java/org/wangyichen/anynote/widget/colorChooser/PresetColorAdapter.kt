package org.wangyichen.anynote.widget.colorChooser

import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import org.wangyichen.anynote.R
import org.wangyichen.anynote.module.AnyNoteApplication.Companion.context

class PresetColorAdapter(private val viewModel: ColorChooserViewModel) :
  RecyclerView.Adapter<PresetColorAdapter.PresetColorViewHolder>() {
  private val colors = context.resources.getIntArray(R.array.preset_colors)
  private var focusIdx = -1

  init {
    checkFocus()
  }

  class PresetColorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val image: ImageView = view.findViewById(R.id.color)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PresetColorViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.item_color, parent, false)
    return PresetColorViewHolder(view)
  }

  override fun getItemCount() = colors.size

  override fun onBindViewHolder(holder: PresetColorViewHolder, position: Int) {
    holder.image.apply {
      val color = colors[position]
      if (position == focusIdx) {
        setImageResource(R.drawable.cycle_focus)
      } else {
        setImageResource(R.drawable.cycle)
      }
      setColorFilter(color, PorterDuff.Mode.MULTIPLY)
      setOnClickListener {
        viewModel.onColorSelected(color)
        checkFocus()
        notifyDataSetChanged()
      }
    }
  }

  //  从自定义切换过来时，刷新
  fun refresh() {
    if (checkFocus()) notifyDataSetChanged()
  }

  //  判断当前颜色是否是预置颜色，当未改变时返回false
  private fun checkFocus(): Boolean {
    var newPos = -1
    for (idx in colors.indices) {
      if (colors[idx] == viewModel.getColor()) {
        newPos = idx
        break
      }
    }
    return if (newPos == focusIdx) false else {
      focusIdx = newPos
      true
    }
  }
}