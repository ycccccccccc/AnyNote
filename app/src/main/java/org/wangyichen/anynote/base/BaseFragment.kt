package org.wangyichen.anynote.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

open class BaseFragment : Fragment() {
  override fun onResume() {
    super.onResume()
    (activity as BaseActivity).currentFragment = this
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    observeLiveData()
  }

  open fun observeLiveData() {}

  //  处理返回事件返回true
  open fun onBackPress(): Boolean = false
}