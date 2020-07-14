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
import org.wangyichen.anynote.source.Entity.Notebook

class NavigatorFragment : BaseFragment() {
  val notebooks = ArrayList<Notebook>()
  lateinit var viewModel: NotesViewModel
  lateinit var adapter: NotebooksAdapter

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
    adapter = NotebooksAdapter(notebooks, viewModel, activity?.findViewById(R.id.drawer_layout)!!)
    items_nav.adapter = adapter
    val manager = LinearLayoutManager(context)
    items_nav.layoutManager = manager
    observeLiveData()
  }

  private fun observeLiveData() {
    viewModel.notebooks.observe(viewLifecycleOwner, Observer {
      notebooks.clear()
      notebooks.addAll(it)
      adapter.notifyDataSetChanged()
    })
  }

  companion object {
    fun newInstance() = NavigatorFragment()
  }
}