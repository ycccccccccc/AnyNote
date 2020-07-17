package org.wangyichen.anynote.module.addEditNote

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.frag_add_edit_note.*
import org.wangyichen.anynote.R
import org.wangyichen.anynote.base.BaseFragment
import org.wangyichen.anynote.databinding.FragAddEditNoteBinding
import org.wangyichen.anynote.utils.ext.showSnackbar

class AddEditNoteFragment : BaseFragment() {
  lateinit var binding: FragAddEditNoteBinding

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = FragAddEditNoteBinding.inflate(inflater, container, false).apply {
      viewmodel = (activity as AddEditNoteActivity).obtainViewModel()
      lifecycleOwner = viewLifecycleOwner
    }
    setHasOptionsMenu(true)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    observeLiveData()
  }

  override fun onResume() {
    super.onResume()
    binding.viewmodel?.start(
      this,
      arguments?.getLong(ARGUMENT_NOTE_ID)!!,
      arguments?.getLong(ARGUMENT_NOTEBOOK_ID)
    )
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.menu_add_edit, menu)
    binding.viewmodel?.run {
      archived.observe(viewLifecycleOwner, Observer {
        menu.findItem(R.id.archive_note).apply {
          setIcon(if (it) R.drawable.archived_white else R.drawable.archive_white)
          setTitle(if (it) "取消归档" else "归档")
        }
      })
      topping.observe(viewLifecycleOwner, Observer {
        menu.findItem(R.id.topping_note).apply {
          setIcon(if (it) R.drawable.toppinged_white else R.drawable.topping_white)
          setTitle(if (it) "取消置顶" else "置顶")
        }
      })
    }
  }

  override fun onOptionsItemSelected(item: MenuItem) =
    when (item.itemId) {
      R.id.topping_note -> {
        binding.viewmodel?.changeTopping()
        true
      }
      R.id.archive_note -> {
        binding.viewmodel?.changeArchived()
        true
      }
      R.id.delete_note -> {
        binding.viewmodel?.deleteNote()
        true
      }
      R.id.cancel_edit -> {
        binding.viewmodel?.cancelEdit()
        true
      }
      R.id.share_note -> {
        true
      }
      R.id.save_image -> {
        true
      }
      else -> super.onOptionsItemSelected(item)

    }

  private fun observeLiveData() {
    binding.viewmodel?.run {
      showSnackBarEvent.observe(viewLifecycleOwner, Observer {
        view?.showSnackbar(it, Snackbar.LENGTH_SHORT)
      })
      color.observe(viewLifecycleOwner, Observer {
        val bg = note_color.background as GradientDrawable
        bg.setColor(it)
      })
    }
  }


  companion object {
    const val ARGUMENT_NOTE_ID = "NOTE_ID"
    const val ARGUMENT_NOTEBOOK_ID = "ARGUMENT_NOTEBOOK_ID"

    fun newInstance(noteId: Long, notebookId: Long) = AddEditNoteFragment().apply {
      arguments = Bundle().apply {
        putLong(ARGUMENT_NOTE_ID, noteId)
        putLong(ARGUMENT_NOTEBOOK_ID, notebookId)
      }
    }
  }
}