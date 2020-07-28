package org.wangyichen.anynote.base

import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {
  var currentFragment: BaseFragment? = null

  override fun onBackPressed() {
    when {
      currentFragment?.onBackPress() ?: false -> { } // 先处理fragment的返回
      onBackPress() -> { } //再处理activity的返回
      else -> super.onBackPressed()
    }
  }

  override fun onPostCreate(savedInstanceState: Bundle?) {
    super.onPostCreate(savedInstanceState)
    observeLiveData()
  }

  open fun observeLiveData() {}

  //  默认直接返回
  open fun onBackPress(): Boolean = false
}