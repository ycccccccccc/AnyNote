package org.wangyichen.anynote.module.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.frag_nav_notebooks.*
import org.wangyichen.anynote.R
import org.wangyichen.anynote.base.BaseFragment
import org.wangyichen.anynote.data.Entity.Notebook

class NavigatorFragment : BaseFragment() {
  val notebooks = ArrayList<Notebook>()
  private lateinit var viewModel: NotesViewModel
  private lateinit var adapter: NotebooksAdapter

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val view = inflater.inflate(R.layout.frag_nav_notebooks, container, false)
    viewModel = (activity as NotesActivity).obtainViewModel()
    return view
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setupAdapter()
  }

  private fun setupAdapter() {
    adapter = NotebooksAdapter(viewModel, activity?.findViewById(R.id.drawer_layout)!!)
    items_nav.adapter = adapter
    val manager = LinearLayoutManager(context)
    items_nav.layoutManager = manager
    items_nav.addItemDecoration(NotebookDecoration())
  }

  override fun observeLiveData() {
    viewModel.notebooks.observe(viewLifecycleOwner, Observer {
      adapter.setNotebooks(it)
    })
  }

  companion object {
    fun newInstance() = NavigatorFragment()
  }
}