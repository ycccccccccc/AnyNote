package org.wangyichen.anynote.module.noteDetail

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import org.wangyichen.anynote.R
import org.wangyichen.anynote.base.BaseFragment
import org.wangyichen.anynote.databinding.FragNoteDetailBinding
import org.wangyichen.anynote.utils.ext.showSnackbar

class NoteDetailFragment : BaseFragment() {
  lateinit var binding: FragNoteDetailBinding

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = FragNoteDetailBinding.inflate(inflater, container, false).apply {
      viewmodel = (activity as NoteDetailActivity).obtainViewModel()
    }
    setHasOptionsMenu(true)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding.lifecycleOwner = viewLifecycleOwner
    setupFAB()
    observeLivedata()
  }

  override fun onResume() {
    super.onResume()
    binding.viewmodel?.start(this)
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.menu_detail, menu)

    binding.viewmodel?.run {
      arvhived.observe(viewLifecycleOwner, Observer {
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
      R.id.share_note -> {
        true
      }
      R.id.delete_note -> {
        binding.viewmodel?.deleteNote()
        true
      }
      R.id.save_image -> {
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  private fun observeLivedata() {
    binding.viewmodel?.run {
      color.observe(viewLifecycleOwner, Observer {
        val bg = binding.noteColor.background as GradientDrawable
        bg.setColor(it)
      })
      showSnackBarEvent.observe(viewLifecycleOwner, Observer {
        view?.showSnackbar(it, Snackbar.LENGTH_SHORT)
      })

    }
  }

  private fun setupFAB() {
    activity?.findViewById<View>(R.id.fab_edit_note)?.setOnClickListener {
      binding.viewmodel?.editNote()
    }
  }


  companion object {
    const val ARGUMENT_NOTE_ID = "TASK_ID"

    fun newInstance(noteId: Long) = NoteDetailFragment().apply {
      arguments = Bundle().apply {
        putLong(ARGUMENT_NOTE_ID, noteId)
      }
    }
  }
}