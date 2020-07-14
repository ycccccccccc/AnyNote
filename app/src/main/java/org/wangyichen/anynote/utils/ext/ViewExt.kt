package org.wangyichen.anynote.utils.ext

import android.view.View
import com.google.android.material.snackbar.Snackbar

fun View.showSnackbar(text: String, timeLength: Int) {
  Snackbar.make(this, text, timeLength).show()
}