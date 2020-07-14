package org.wangyichen.anynote.module.notes

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.frag_notes.*
import org.wangyichen.anynote.R
import org.wangyichen.anynote.base.BaseFragment
import org.wangyichen.anynote.databinding.FragNotesBinding
import org.wangyichen.anynote.source.Entity.Note
import org.wangyichen.anynote.source.Entity.NoteWithOthers
import org.wangyichen.anynote.source.Entity.Notebook
import org.wangyichen.anynote.utils.ConfermDialogFragment
import org.wangyichen.anynote.utils.TimeUtils
import org.wangyichen.anynote.utils.constant.FilterType
import org.wangyichen.anynote.utils.constant.SortType
import org.wangyichen.anynote.utils.ext.showSnackbar

class NotesFragment : BaseFragment() {
  var notes = ArrayList<NoteWithOthers>()
  var notebooks = ArrayList<Notebook>()

  private lateinit var binding: FragNotesBinding
  private lateinit var adapter: NotesAdapter
//  private lateinit var menu: Menu

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = FragNotesBinding.inflate(inflater, container, false).apply {
      viewmodel = (activity as NotesActivity).obtainViewModel()
    }
    setHasOptionsMenu(true)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding.lifecycleOwner = viewLifecycleOwner
    setupAdapter()
    observeLivedata()
  }

  override fun onOptionsItemSelected(item: MenuItem) =
    when (item.itemId) {
      //    sort
      R.id.creation -> {
        binding.viewmodel?.setSort(SortType.CREATION)
        true
      }
      R.id.lastModification -> {
        binding.viewmodel?.setSort(SortType.LASTMODIFICATION)
        true
      }

      //    filter
      R.id.hasImage -> {
        binding.viewmodel?.setFilter(FilterType.HAS_IMAGE)
        true
      }
      R.id.noImage -> {
        binding.viewmodel?.setFilter(FilterType.NO_IMAGE)
        true
      }
      else -> super.onOptionsItemSelected(item)

    }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.menu_notes, menu)
//    TODO
//    binding.viewmodel?.isTrash?.observe(viewLifecycleOwner, Observer { isTrash ->
//      menu.findItem(R.id.clear_trash).setVisible(isTrash)
//    })
  }

  override fun onResume() {
    super.onResume()
    binding.viewmodel?.start(this)
  }

  override fun onStop() {
    super.onStop()
    binding.viewmodel?.onstop()
  }

  private fun setupAdapter() {
    val listener = object : NotesItemActionListener {
      override fun onNoteClicked(note: Note) {
        binding.viewmodel?.openNoteDetails(note)
      }

      override fun onToppingClicked(note: Note): Boolean {
        var conferm = false
        val listener = object : ConfermDialogFragment.ConfermListener {
          override fun onPostive() {
            binding.viewmodel?.unToppingNote(note)
            conferm = true
          }

          override fun onNegtive() {
          }
        }
        ConfermDialogFragment("是否取消置顶", "", listener).show(parentFragmentManager, "untopping")
        return conferm
      }
    }

    adapter = NotesAdapter(notes, notebooks, listener)
    notes_list.adapter = adapter
    val manager = LinearLayoutManager(context)
    notes_list.layoutManager = manager
  }

  private fun observeLivedata() {
    binding.viewmodel?.run {
      snackbarEvent.observe(viewLifecycleOwner, Observer {
        view?.showSnackbar(it, Snackbar.LENGTH_SHORT)
      })
      notes.observe(viewLifecycleOwner, Observer {
        this@NotesFragment.notes.clear()
        this@NotesFragment.notes.addAll(it)
        adapter.notifyDataSetChanged()
      })
      notebooks.observe(viewLifecycleOwner, Observer {
        this@NotesFragment.notebooks.clear()
        this@NotesFragment.notebooks.addAll(it)
        adapter.notifyDataSetChanged()
      })
      sortChangeEvent.observe(viewLifecycleOwner, Observer {
        adapter.sort = it
      })
    }
  }

  companion object {
    fun newInstance() = NotesFragment()
  }
}