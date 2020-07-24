package org.wangyichen.anynote.widget.colorChooser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.dialog_color_chooser.*
import org.wangyichen.anynote.R
import org.wangyichen.anynote.databinding.DialogColorChooserBinding
import org.wangyichen.anynote.utils.SystemUtils

class ColorChooserDialog : DialogFragment() {
  private lateinit var owner: ViewModelStoreOwner
  lateinit var binding: DialogColorChooserBinding
  lateinit var listener: SaveColorListener
  private lateinit var adapter:PresetColorAdapter

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = DialogColorChooserBinding.inflate(inflater, container, false).apply {
      viewmodel = ColorChooserViewModel.getInstance(owner)
    }
    binding.lifecycleOwner = this
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setUpSeekBar()
    observeLiveData()
    binding.viewmodel?.start(arguments?.getInt(COLOR)!!)
    setupAdapter()
  }

  override fun onResume() {
    super.onResume()
    val width =
      SystemUtils.getWidthByPercentage(getString(R.string.color_choose_dialog_width).toFloat())
    val height =
      SystemUtils.getHeightByPercentage(getString(R.string.color_choose_dialog_height).toFloat())
    dialog?.window?.setLayout(width, height)
  }

//  自定义颜色
  private fun setUpSeekBar() {
    val listener = object : SeekBar.OnSeekBarChangeListener {
      override fun onProgressChanged(p0: SeekBar, p1: Int, p2: Boolean) {
        when (p0.id) {
          binding.seekbarR.id -> binding.viewmodel?.onPregressChanged(
            ColorChooserViewModel.SEEKBAR_R, p1, p2
          )
          binding.seekbarG.id -> binding.viewmodel?.onPregressChanged(
            ColorChooserViewModel.SEEKBAR_G, p1, p2
          )
          binding.seekbarB.id -> binding.viewmodel?.onPregressChanged(
            ColorChooserViewModel.SEEKBAR_B, p1, p2
          )
        }
      }

      override fun onStartTrackingTouch(p0: SeekBar?) {
      }

      override fun onStopTrackingTouch(p0: SeekBar?) {
      }
    }
    seekbar_r.setOnSeekBarChangeListener(listener)
    seekbar_g.setOnSeekBarChangeListener(listener)
    seekbar_b.setOnSeekBarChangeListener(listener)
  }

  private fun setupAdapter() {
    adapter = PresetColorAdapter(binding.viewmodel!!)
    color_lists.adapter = adapter
    color_lists.layoutManager = GridLayoutManager(context, 4)
  }


  private fun observeLiveData() {
    binding.viewmodel?.run {
      colorChangeEvent.observe(viewLifecycleOwner, Observer {
        custom_color.setColorFilter(it)
      })
      closeEvent.observe(viewLifecycleOwner, Observer {
        val content = it.getContent()
        if (content != null) {
          this@ColorChooserDialog.dismiss()
        }
      })
      saveEvent.observe(viewLifecycleOwner, Observer {
        val content = it.getContent()
        if (content != null) {
          listener.onSave(content)
          this@ColorChooserDialog.dismiss()
        }
      })
      showPreset.observe(viewLifecycleOwner, Observer {
        if (it) {
          adapter.refresh()
        }
      })
    }
  }

  companion object {
    const val COLOR = "color"

    fun getInstance(owner: ViewModelStoreOwner, color: Int, listener: SaveColorListener) =
      ColorChooserDialog().apply {
        this.owner = owner
        this.listener = listener
        arguments = Bundle().apply {
          putInt(COLOR, color)
        }
      }
  }

  interface SaveColorListener {
    fun onSave(color: Int)
  }
}