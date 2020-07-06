package org.wangyichen.anynote.module.mainpage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_main.*
import org.wangyichen.anynote.R
import org.wangyichen.anynote.base.BaseActivity

class MainActivity : BaseActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    val vm = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)
    button.setOnClickListener {
      vm.addNote()
    }
    vm.getNotes()
    vm.getNotes()
    vm.notes?.observe(this, Observer { textView.text = it.size.toString() })

  }
}