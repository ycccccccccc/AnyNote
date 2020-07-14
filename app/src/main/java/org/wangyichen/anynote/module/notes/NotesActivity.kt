package org.wangyichen.anynote.module.notes

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_notes.*
import org.wangyichen.anynote.R
import org.wangyichen.anynote.base.BaseActivity
import org.wangyichen.anynote.databinding.ActivityNotesBinding
import org.wangyichen.anynote.module.addEditNote.AddEditNoteActivity
import org.wangyichen.anynote.module.noteDetail.NoteDetailActivity
import org.wangyichen.anynote.source.Entity.Note
import org.wangyichen.anynote.source.Entity.Notebook
import org.wangyichen.anynote.source.local.Repository
import org.wangyichen.anynote.utils.IntentUtils
import org.wangyichen.anynote.utils.TimeUtils
import org.wangyichen.anynote.utils.ext.setupActionBar

class NotesActivity : BaseActivity(), NotesNavigator, NotesItemNavigator {
  lateinit var binding: ActivityNotesBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, R.layout.activity_notes)
    binding.lifecycleOwner = this

    setupActionBar(R.id.toolbar) {
      titleActionBar.apply {
        setOnClickListener {
          drawer_layout.openDrawer(GravityCompat.START)
        }
      }
    }

    binding.viewmodel = obtainViewModel().apply {
      openNoteEvent.observe(this@NotesActivity, Observer { id ->
        this@NotesActivity.openNoteDetails(id)
      })
      addNoteEvent.observe(this@NotesActivity, Observer {
        this@NotesActivity.addNote()
      })
    }

    setupNavaigationDrawer()
    setupViewFragment()
    setupFab()
  }

  private fun setupViewFragment() {
    supportFragmentManager.beginTransaction().apply {
      replace(R.id.contentFrame, NotesFragment.newInstance())
      commit()
    }
  }

  private fun setupNavaigationDrawer() {
    supportFragmentManager.beginTransaction().apply {
      replace(R.id.notebook_navigation, NavigatorFragment.newInstance())
      commit()
    }
  }


  private fun setupFab() {
    binding.fabAddNote.setOnClickListener {
      binding.viewmodel?.addNewNote()
    }
  }

  override fun addNote() {
    val intent = Intent(this, AddEditNoteActivity::class.java)
    IntentUtils.startActivity(this, intent)
  }


  override fun openNoteDetails(id: Long) {
    val intent = Intent(this, NoteDetailActivity::class.java)
    intent.putExtra(NoteDetailActivity.EXTRA_NOTE_ID, id)
    IntentUtils.startActivityForResult(
      this,
      intent,
      IntentUtils.REQUEST_CODE_ADD_EDIT_NOTE_ACTIVITY
    )
  }


  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    binding.viewmodel?.handleActivityResult(requestCode, resultCode)
  }

  fun obtainViewModel() = NotesViewModel.getInstance(this)
}