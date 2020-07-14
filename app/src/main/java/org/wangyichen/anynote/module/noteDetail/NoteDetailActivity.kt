package org.wangyichen.anynote.module.noteDetail

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.Observer
import org.wangyichen.anynote.R
import org.wangyichen.anynote.base.BaseActivity
import org.wangyichen.anynote.module.addEditNote.AddEditNoteActivity
import org.wangyichen.anynote.utils.IntentUtils
import org.wangyichen.anynote.utils.ext.setupActionBar

class NoteDetailActivity : BaseActivity(), NoteDetailNavigator {
  lateinit var viewModel: NoteDetailViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_note_detail)

    viewModel = obtainViewModel().apply {
      deleteNoteEvent.observe(this@NoteDetailActivity, Observer {
        ondeletedNote()
      })
      addEditNoteEvent.observe(this@NoteDetailActivity, Observer {
        editNote(it)
      })
    }

    setupActionBar(R.id.toolbar) {
      setDisplayHomeAsUpEnabled(true)
    }
    setupViewFragment()
  }

  private fun setupViewFragment() {
    supportFragmentManager.beginTransaction().apply {
      replace(R.id.contentFrame, NoteDetailFragment.newInstance(0))
      commit()
    }
  }


  override fun onOptionsItemSelected(item: MenuItem) =
    when (item.itemId) {
      android.R.id.home -> {
        super.onBackPressed()
        true
      }
      else -> super.onOptionsItemSelected(item)
    }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    viewModel.handleResult(requestCode, resultCode)
  }

  fun obtainViewModel() =
    NoteDetailViewModel.getInstance(this, intent.getLongExtra(EXTRA_NOTE_ID, 0))

  companion object {
    val EXTRA_NOTE_ID = "NOTE_ID"
  }

  override fun editNote(noteId: Long) {
    val intent = Intent(this, AddEditNoteActivity::class.java).apply {
      putExtra(AddEditNoteActivity.EXTRA_NOTE_ID, noteId)
    }
    IntentUtils.startActivityForResult(
      this,
      intent,
      IntentUtils.REQUEST_CODE_ADD_EDIT_NOTE_ACTIVITY
    )
  }

  override fun ondeletedNote() {
    setResult(IntentUtils.DELETE_RESULT_OK)
    finish()
  }
}