package org.wangyichen.anynote.module.addEditNote

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.frag_note_detail.*
import org.wangyichen.anynote.R
import org.wangyichen.anynote.base.BaseActivity
import org.wangyichen.anynote.module.AnyNoteApplication
import org.wangyichen.anynote.module.DEFAULT_NOTEBOOK_ID
import org.wangyichen.anynote.utils.IntentUtils
import org.wangyichen.anynote.utils.ext.setupActionBar
import org.wangyichen.anynote.utils.ext.showSnackbar

class AddEditNoteActivity : BaseActivity(), AddEditNoteNavigator {
  private val REQUEST_ALBUM = 0

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_add_edit_note)
    setupActionBar(R.id.toolbar) {
      setHomeAsUpIndicator(R.drawable.yes_white)
      setDisplayHomeAsUpEnabled(true)
    }
    setupViewFragment()
  }

  override fun observeLiveData() {
    obtainViewModel().run {
      saveNoteEvent.observe(this@AddEditNoteActivity, Observer {
        onSaveNote(it)
      })
      deleteNoteEvent.observe(this@AddEditNoteActivity, Observer {
        onDeleteNote()
      })
      addImageEvent.observe(this@AddEditNoteActivity, Observer {
        this@AddEditNoteActivity.addImage()
      })
    }
  }

  private fun setupViewFragment() {
    supportFragmentManager.beginTransaction().apply {
      replace(
        R.id.contentFrame,
        AddEditNoteFragment.newInstance(
          intent.getStringExtra(EXTRA_NOTE_ID)?:"",
          intent.getLongExtra(EXTRA_NOTEBOOK_ID, DEFAULT_NOTEBOOK_ID)
        )
      )
      commit()
    }
  }


  private fun addImage() {

    val intent = Intent(Intent.ACTION_PICK, null)
    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
    startActivityForResult(intent, IntentUtils.OPEN_ALBUM)
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//      if (ContextCompat.checkSelfPermission(
//          AnyNoteApplication.context,
//          Manifest.permission.READ_EXTERNAL_STORAGE
//        ) != PackageManager.PERMISSION_GRANTED
//      ) {
//        requestPermissions(
//          arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
//          REQUEST_ALBUM
//        )
//      } else {
//        onPermissionRequestSuccess(REQUEST_ALBUM)
//      }
//    } else {
//      onPermissionRequestSuccess(REQUEST_ALBUM)
//    }
  }

  private fun onPermissionRequestSuccess(requestCode: Int) {
    when (requestCode) {
      REQUEST_ALBUM -> {
        val intent = Intent(Intent.ACTION_PICK, null)
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        startActivityForResult(intent, IntentUtils.OPEN_ALBUM)
      }
    }
  }

  private fun onPermissionRequestFail(requestCode: Int) {
    when (requestCode) {
      REQUEST_ALBUM -> {
        content.showSnackbar("需要权限才可选取照片",Snackbar.LENGTH_SHORT)
      }
    }
  }

  override fun onOptionsItemSelected(item: MenuItem) =
    when (item.itemId) {
      android.R.id.home -> {
        obtainViewModel().saveNote()
        true
      }
      else -> super.onOptionsItemSelected(item)
    }

  override fun onDeleteNote() {
    setResult(IntentUtils.DELETE_RESULT_OK)
    finish()
  }

  fun obtainViewModel() = AddEditNoteViewModel.newInstance(this)

  override fun onSaveNote(state: Int) {
    when (state) {
      AddEditNoteViewModel.SKETCH -> setResult(IntentUtils.ADD_SKETCH_OK)
      AddEditNoteViewModel.SAVE -> setResult(IntentUtils.ADD_EDIT_RESULT_OK)
      else -> {
      }
    }
    finish()
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ) {
    when (requestCode) {
      REQUEST_ALBUM -> {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          onPermissionRequestSuccess(REQUEST_ALBUM)
        } else {
          onPermissionRequestFail(REQUEST_ALBUM)
        }
      }
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    obtainViewModel().handleResult(requestCode, resultCode, data)
  }

  override fun onBackPressed() {
    obtainViewModel().backPressed()
  }

  companion object {
    const val EXTRA_NOTE_ID = "NOTE_ID"
    const val EXTRA_NOTEBOOK_ID = "EXTRA_NOTEBOOK_ID"
  }
}