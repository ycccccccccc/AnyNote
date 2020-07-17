package org.wangyichen.anynote.widget.colorChooser

import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import org.wangyichen.anynote.R
import org.wangyichen.anynote.module.AnyNoteApplication.Companion.context

class PresetColorAdapter(val viewModel: ColorChooserViewModel) :
  RecyclerView.Adapter<PresetColorAdapter.PresetColorViewHolder>() {
  val colors = context.resources.getIntArray(R.array.preset_colors)

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
      drawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
      setOnClickListener {
        viewModel.onColorSelected(color)
      }
      if(color == viewModel.getColor()) requestFocus()
      setOnFocusChangeListener { view, hasFocus ->
        if (hasFocus){
          if (view.isInTouchMode) {
            performClick()
          }
        }
      }
    }
  }

}