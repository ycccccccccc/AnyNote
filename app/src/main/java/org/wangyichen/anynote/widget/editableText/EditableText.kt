package org.wangyichen.anynote.widget.editableText

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet

//  https://blog.csdn.net/drkcore/article/details/53440392
class EditableText(context: Context, attrs: AttributeSet) :
  androidx.appcompat.widget.AppCompatEditText(context, attrs) {
  private val manager = OperationManager(this)

  init {
    addTextChangedListener(manager)
  }

  fun redo() {
    manager.redo()
  }

  fun undo() {
    manager.undo()
  }

//  override fun onSaveInstanceState(): Parcelable? = Bundle().apply {
//    putParcelable(KEY_SUPER,super.onSaveInstanceState())
//    putBundle(KEY_OPT,manager.saveState())
//  }
//
//  override fun onRestoreInstanceState(state: Parcelable?) {
//    val bundle = state as Bundle
//    super.onRestoreInstanceState(bundle.getParcelable(KEY_SUPER))
//    manager.loadState(bundle.getBundle(KEY_OPT)!!)
//  }

  companion object {
    private const val KEY_SUPER = "KEY_SUPER"
    private val KEY_OPT = "KEY_OPT"
  }
}