package org.wangyichen.anynote.module.addEditNote

import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.Observer
import org.wangyichen.anynote.R
import org.wangyichen.anynote.base.BaseActivity
import org.wangyichen.anynote.module.DEFAULT_NOTEBOOK_ID
import org.wangyichen.anynote.utils.IntentUtils
import org.wangyichen.anynote.utils.ext.setupActionBar

class AddEditNoteActivity : BaseActivity(), AddEditNoteNavigator {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_add_edit_note)
    setupActionBar(R.id.toolbar) {
      setHomeAsUpIndicator(R.drawable.yes_white)
      setDisplayHomeAsUpEnabled(true)
    }
    observeLivedata()
    setupViewFragment()
  }

  private fun observeLivedata() {
    obtainViewModel().run {
      saveNoteEvent.observe(this@AddEditNoteActivity, Observer {
        onSaveNote(it)
      })
      deleteNoteEvent.observe(this@AddEditNoteActivity, Observer {
        onDeleteNote()
      })
    }
  }

  private fun setupViewFragment() {
    supportFragmentManager.beginTransaction().apply {
      replace(
        R.id.contentFrame,
        AddEditNoteFragment.newInstance(
          intent.getLongExtra(EXTRA_NOTE_ID, -1L),
          intent.getLongExtra(EXTRA_NOTEBOOK_ID, DEFAULT_NOTEBOOK_ID)
        )
      )
      commit()
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


  override fun onBackPressed() {
    obtainViewModel().backPressed()
  }

  companion object {
    const val EXTRA_NOTE_ID = "NOTE_ID"
    const val EXTRA_NOTEBOOK_ID = "EXTRA_NOTEBOOK_ID"
  }
}