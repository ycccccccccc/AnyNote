package org.wangyichen.anynote.widget.colorChooser

import android.graphics.Color
import androidx.lifecycle.*
import org.wangyichen.anynote.Event
import org.wangyichen.anynote.module.AnyNoteApplication.Companion.context
import org.wangyichen.anynote.R

class ColorChooserViewModel : ViewModel() {
  private val colors = context.resources.getIntArray(R.array.preset_colors)

  private val _showPreset = MutableLiveData<Boolean>()
  val showPreset: LiveData<Boolean>
    get() = _showPreset

  private val _customBtnText = MutableLiveData<String>()
  val customBtnText: LiveData<String>
    get() = _customBtnText

  val progressR = MutableLiveData<Int>()
  val progressG = MutableLiveData<Int>()
  val progressB = MutableLiveData<Int>()

  private val _colorChangeEvent = MutableLiveData<Int>()
  val colorChangeEvent: LiveData<Int>
    get() = _colorChangeEvent

  private val _closeEvent = MutableLiveData<Event<Any>>()
  val closeEvent: LiveData<Event<Any>>
    get() = _closeEvent

  private val _saveEvent = MutableLiveData<Event<Int>>()
  val saveEvent: LiveData<Event<Int>>
    get() = _saveEvent

  fun onSwitchClick() {
    _showPreset.value = !_showPreset.value!!
    _customBtnText.value = if (_showPreset.value!!) "自定义" else {
      _colorChangeEvent.value = getColor()
      "预置"
    }
  }

  fun onCancelClick() {
    _closeEvent.value = Event(Any())
  }

  fun onSaveClick() {
    _saveEvent.value = Event(getColor())
  }

  fun start(color: Int) {
    if (colors.contains(color)) {
      _showPreset.value = true
      _customBtnText.value = "自定义"
    } else {
      _showPreset.value = false
      _customBtnText.value = "预置"
    }
    progressR.value = Color.red(color)
    progressG.value = Color.green(color)
    progressB.value = Color.blue(color)
    _colorChangeEvent.value = getColor()
  }

  fun onColorSelected(color: Int) {
    progressR.value = Color.red(color)
    progressG.value = Color.green(color)
    progressB.value = Color.blue(color)
  }

  fun onPregressChanged(seekBar: Int, progress: Int, fromUser: Boolean) {
    if (fromUser) {
      when (seekBar) {
        SEEKBAR_R -> progressR.value = progress
        SEEKBAR_G -> progressG.value = progress
        SEEKBAR_B -> progressB.value = progress
      }
      _colorChangeEvent.value = getColor()
    }
  }

  fun getColor() = Color.rgb(
    progressR.value!!,
    progressG.value!!,
    progressB.value!!
  )

  companion object {
    const val SEEKBAR_R = 0
    const val SEEKBAR_G = 1
    const val SEEKBAR_B = 2

    fun getInstance(owner: ViewModelStoreOwner) =
      ViewModelProvider(owner).get(ColorChooserViewModel::class.java)
  }
}