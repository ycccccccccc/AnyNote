package org.wangyichen.anynote.utils.ext

import androidx.annotation.IdRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

fun AppCompatActivity.setupActionBar(@IdRes resId: Int, action: ActionBar.() -> Unit) {
  setSupportActionBar(findViewById(resId))
  supportActionBar?.run {
    action()
    setDisplayShowTitleEnabled(false)
  }
}

