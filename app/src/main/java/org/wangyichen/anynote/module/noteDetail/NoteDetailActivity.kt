package org.wangyichen.anynote.module.noteDetail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.Observer
import org.wangyichen.anynote.R
import org.wangyichen.anynote.base.BaseActivity
import org.wangyichen.anynote.module.addEditNote.AddEditNoteActivity
import org.wangyichen.anynote.module.notes.NotesActivity
import org.wangyichen.anynote.utils.IntentUtils
import org.wangyichen.anynote.utils.ext.setupActionBar
import org.wangyichen.anynote.widget.ShowCoverImageActivity

class NoteDetailActivity : BaseActivity(), NoteDetailNavigator {
  private lateinit var viewModel: NoteDetailViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    viewModel = obtainViewModel()
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_note_detail)

    setupActionBar(R.id.toolbar) {
      setDisplayHomeAsUpEnabled(true)
    }
    setupViewFragment()
  }

  private fun setupViewFragment() {
    supportFragmentManager.beginTransaction().apply {
      replace(R.id.contentFrame, NoteDetailFragment.newInstance(""))
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

//  从通知跳转进来时，返回主页
  override fun onBackPress() =
    if (isTaskRoot) {
      val intent = Intent(this, NotesActivity::class.java)
      intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
      IntentUtils.startActivityOnBack(this, intent)
      true
    } else {
      false
    }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    viewModel.handleResult(requestCode, resultCode)
  }

  override fun observeLiveData() {
    viewModel.run {
      deleteNoteEvent.observe(this@NoteDetailActivity, Observer {
        onDeletedNote()
      })
      addEditNoteEvent.observe(this@NoteDetailActivity, Observer {
        editNote(it)
      })
      shareNoteEvent.observe(this@NoteDetailActivity, Observer {
        val content = it.getContent()
        if (content != null) {
          shareNote(content)
        }
      })
      showCoverEvent.observe(this@NoteDetailActivity, Observer {
        val content = it.getContent()
        if (content != null) {
          showCover(content)
        }
      })
    }
  }

  fun obtainViewModel() =
    NoteDetailViewModel.getInstance(this, intent.getStringExtra(EXTRA_NOTE_ID) ?: "")

  private fun shareNote(uri: Uri) {
    val intent = Intent(Intent.ACTION_SEND).apply {
      putExtra(Intent.EXTRA_STREAM, uri)
      type = "image/png"
    }
    startActivity(intent)
  }

  override fun showCover(uri: Uri) {
    val intent = Intent(this, ShowCoverImageActivity::class.java).apply {
      putExtra(ShowCoverImageActivity.EXTRA_URI, uri)
    }
    IntentUtils.startActivity(this, intent)
  }

  override fun editNote(noteId: String) {
    val intent = Intent(this, AddEditNoteActivity::class.java).apply {
      putExtra(AddEditNoteActivity.EXTRA_NOTE_ID, noteId)
    }
    IntentUtils.startActivityForResult(
      this,
      intent,
      IntentUtils.REQUEST_CODE_ADD_EDIT_NOTE_ACTIVITY
    )
  }

  override fun onDeletedNote() {
    setResult(IntentUtils.DELETE_RESULT_OK)
    finish()
  }

  companion object {
    val EXTRA_NOTE_ID = "NOTE_ID"
  }
}