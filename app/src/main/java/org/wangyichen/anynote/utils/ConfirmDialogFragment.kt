package org.wangyichen.anynote.utils

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

class ConfirmDialogFragment(val title: String, val message: String, val listener: ConfirmListener) :
  DialogFragment() {
  interface ConfirmListener {
    fun onPositive()
    fun onNegative()
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val builder = AlertDialog.Builder(context!!).apply {
      setTitle(title)
      setMessage(message)
      setPositiveButton("确定") { _, _ -> listener.onPositive() }
      setNegativeButton("取消") { _, _ -> listener.onNegative() }
    }
    return builder.create();
  }

  companion object {
    fun show(
      title: String,
      message: String,
      listener: ConfirmListener,
      manager: FragmentManager,
      tag: String
    ) {
      ConfirmDialogFragment(title, message, listener).show(manager, tag)
    }
  }
}
