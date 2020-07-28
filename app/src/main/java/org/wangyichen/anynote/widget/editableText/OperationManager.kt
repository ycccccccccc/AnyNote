package org.wangyichen.anynote.widget.editableText

import android.os.Bundle
import android.os.Parcel
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.util.*

//  https://blog.csdn.net/drkcore/article/details/53440392
class OperationManager(val editText: EditText) : TextWatcher {
  private val undoStack = LinkedList<EditOperation>()
  private val redoStack = LinkedList<EditOperation>()
  private var enable = true
  private var operation: EditOperation? = null

  private fun disable() {
    enable = false
  }

  private fun enable() {
    enable = true
  }

  private fun canRedo() = redoStack.isNotEmpty()
  private fun canUndo() = undoStack.isNotEmpty()

  fun redo() {
    if (canRedo()) {
      val option = redoStack.pop()

      disable() //屏蔽重做的改变
      option.redo(editText)
      enable()

      undoStack.push(option)
    }
  }


  fun undo() {
    if (canUndo()) {
      val option = undoStack.pop()

      disable()
      option.undo(editText)
      enable()

      redoStack.push(option)
    }
  }

  override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    if (count > 0) {
      if (enable) {
        if (operation == null) operation = EditOperation()
        val end = start + count
        operation!!.setSrc(s?.subSequence(start, end), start, end)
      }
    }
  }

  override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    if (count > 0) {
      if (enable) {
        if (operation == null) operation = EditOperation()
        val end = start + count
        operation!!.setDst(s?.subSequence(start, end), start, end)
      }
    }
  }

  override fun afterTextChanged(s: Editable?) {
    //  有更改时清空重做栈
    if (enable && operation != null) {
      redoStack.clear()
      undoStack.push(operation)
      operation = null
    }
  }

//  fun saveState() = Bundle().apply {
//    putSerializable(KEY_UNDO_OPTS,undoStack)
//    putSerializable(KEY_REDO_OPTS,redoStack)
//  }
//
//  fun loadState(state: Bundle) {
//    val undoOpts = state.getSerializable(KEY_UNDO_OPTS) as Collection<EditOperation>
//    undoStack.clear()
//    undoStack.addAll(undoOpts)
//
//    val redoOpts = state.getSerializable(KEY_REDO_OPTS) as Collection<EditOperation>
//    redoStack.clear()
//    redoStack.addAll(redoOpts)
//  }

  companion object {
    private const val KEY_UNDO_OPTS = "KEY_UNDO_OPTS"
    private const val KEY_REDO_OPTS = "KEY_REDO_OPTS"
  }
}