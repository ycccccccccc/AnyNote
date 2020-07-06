package org.wangyichen.anynote.utils

import androidx.annotation.IdRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

class AppCompatActivityExt {
  fun AppCompatActivity.setupActionBar(@IdRes resId: Int, action: ActionBar.() -> Unit) {
    setSupportActionBar(findViewById(resId))
    supportActionBar?.run {
      action()
    }
  }

  fun <T : ViewModel> AppCompatActivity.obtainViewModel(viewModelClass: Class<T>) =
    ViewModelProvider(this).get(viewModelClass)

}