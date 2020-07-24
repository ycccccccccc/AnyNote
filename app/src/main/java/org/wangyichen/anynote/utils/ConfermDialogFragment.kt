package org.wangyichen.anynote.utils

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

class ConfermDialogFragment(val title: String, val message: String, val listener: ConfermListener) :
  DialogFragment() {
  interface ConfermListener {
    fun onPositive()
    fun onNegtive()
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val builder = AlertDialog.Builder(context!!).apply {
      setTitle(title)
      setMessage(message)
      setPositiveButton("确定", object : DialogInterface.OnClickListener {
        override fun onClick(p0: DialogInterface?, p1: Int) {
          listener.onPositive()
        }
      })
      setNegativeButton("取消", object : DialogInterface.OnClickListener {
        override fun onClick(p0: DialogInterface?, p1: Int) {
          listener.onNegtive()
        }
      })
    }
    return builder.create();
  }

  companion object {
    fun show(
      title: String,
      message: String,
      listener: ConfermListener,
      manager: FragmentManager,
      tag: String
    ) {
      ConfermDialogFragment(title, message, listener).show(manager,tag)
    }
  }
}
