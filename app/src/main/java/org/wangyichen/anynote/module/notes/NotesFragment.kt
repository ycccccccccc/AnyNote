package org.wangyichen.anynote.module.notes

import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.frag_notes.*
import org.wangyichen.anynote.R
import org.wangyichen.anynote.base.BaseFragment
import org.wangyichen.anynote.databinding.FragNotesBinding
import org.wangyichen.anynote.utils.constant.NotebookIdExt
import org.wangyichen.anynote.utils.constant.SortType
import org.wangyichen.anynote.utils.ext.showSnackbar

class NotesFragment : BaseFragment() {
  private lateinit var menu: Menu
  private lateinit var binding: FragNotesBinding
  private lateinit var adapter: NotesAdapter

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
      // 清空回收站
      R.id.clear_trash -> {
        binding.viewmodel?.clearTrash()
        true
      }
      // action mode item
      R.id.select_all->{
        adapter.selectAll()
        true
      }
      R.id.cancel->{
        closeActionMode()
        true
      }
      else -> super.onOptionsItemSelected(item)

    }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    this.menu = menu
    inflater.inflate(R.menu.menu_notes, menu)

    binding.viewmodel?.notebookType?.observe(viewLifecycleOwner, Observer { type ->
      when (type) {
        NotebookIdExt.ALLNOTES ->
          menu.findItem(R.id.clear_trash).isVisible = false
        NotebookIdExt.ARCHIVED ->
          menu.findItem(R.id.clear_trash).isVisible = false
        NotebookIdExt.SKETCH ->
          menu.findItem(R.id.clear_trash).isVisible = false
        NotebookIdExt.TRASH ->
          menu.findItem(R.id.clear_trash).isVisible = true
      }
    })
//    actionmode开启时，隐藏菜单选项
    binding.viewmodel?.actionMode?.observe(viewLifecycleOwner, Observer {
      if (it) {
        menu.findItem(R.id.sort).isVisible = false
        menu.findItem(R.id.clear_trash).isVisible = false
        menu.findItem(R.id.cancel).isVisible = true
        menu.findItem(R.id.select_all).isVisible = true
      } else {
        menu.findItem(R.id.sort).isVisible = true
        menu.findItem(R.id.cancel).isVisible = false
        menu.findItem(R.id.select_all).isVisible = false
      }
    })

  }

  override fun onResume() {
    super.onResume()
    binding.viewmodel?.start(this)
  }

  override fun onStop() {
    super.onStop()
    binding.viewmodel?.onstop()
  }

  override fun onBackPress(): Boolean =
    if (adapter.isActionMode) {
      closeActionMode()
      true
    } else {
      false
    }


  private fun setupAdapter() {
    adapter = NotesAdapter(binding.viewmodel!!)
    notes_list.adapter = adapter
    val manager = LinearLayoutManager(context)
    notes_list.layoutManager = manager
    notes_list.addItemDecoration(NoteDecoration())
  }

  override fun observeLiveData() {
    binding.viewmodel?.run {
      snackbarEvent.observe(viewLifecycleOwner, Observer {
        view?.showSnackbar(it, Snackbar.LENGTH_SHORT)
      })
      notes.observe(viewLifecycleOwner, Observer {
        adapter.setNotes(it)
      })
      notebooks.observe(viewLifecycleOwner, Observer {
        adapter.setNotebooks(it)
      })
      sortChangeEvent.observe(viewLifecycleOwner, Observer {
        adapter.sort = it
      })
      actionMode.observe(viewLifecycleOwner, Observer {
        if (!it) adapter.closeActionMode()
      })
    }
  }

  private fun closeActionMode() {
    binding.viewmodel?.closeActionMode()
    adapter.closeActionMode()
  }

  companion object {
    fun newInstance() = NotesFragment()
  }
}