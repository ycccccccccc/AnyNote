package org.wangyichen.anynote.module.noteDetail

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.frag_note_detail.*
import org.wangyichen.anynote.R
import org.wangyichen.anynote.base.BaseFragment
import org.wangyichen.anynote.databinding.FragNoteDetailBinding
import org.wangyichen.anynote.module.AnyNoteApplication
import org.wangyichen.anynote.source.local.Repository
import org.wangyichen.anynote.source.local.repository.CoverRepository
import org.wangyichen.anynote.utils.ViewDrawUtils
import org.wangyichen.anynote.utils.ext.showSnackbar

class NoteDetailFragment : BaseFragment() {
  lateinit var binding: FragNoteDetailBinding
  val repository = Repository.getInstance(AnyNoteApplication.context)

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
      trashed.observe(viewLifecycleOwner, Observer { isTrash ->
        if (isTrash) {
          menu.findItem(R.id.topping_note).isVisible = false
          menu.findItem(R.id.archive_note).isVisible = false
          menu.findItem(R.id.share_note).isVisible = false
          menu.findItem(R.id.save_image).isVisible = false
          menu.findItem(R.id.restore_note).isVisible = true
        } else {
          menu.findItem(R.id.topping_note).isVisible = true
          menu.findItem(R.id.archive_note).isVisible = true
          menu.findItem(R.id.share_note).isVisible = true
          menu.findItem(R.id.save_image).isVisible = true
          menu.findItem(R.id.restore_note).isVisible = false
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
        shareImage()
        true
      }
      R.id.delete_note -> {
        binding.viewmodel?.deleteNote()
        true
      }
      R.id.save_image -> {
        saveImage()
        true
      }
      R.id.restore_note ->{
        binding.viewmodel?.restoreNote()
        true
      }
      else -> super.onOptionsItemSelected(item)
    }

  private fun saveImage() {
    ViewDrawUtils.draw(scroll_layout.getChildAt(0)) { bitmap ->
      repository.COVER.saveImageToDCIM(bitmap, binding.viewmodel!!.title.value!!)
      view?.showSnackbar("图片已保存到本地", Snackbar.LENGTH_SHORT)
    }
  }

  private fun shareImage() {
    ViewDrawUtils.draw(scroll_layout.getChildAt(0)) { bitmap ->
      val uri = repository.COVER.saveImageToCache(bitmap)
      binding.viewmodel?.shareImage(bitmap, uri)
    }
  }


  override fun observeLiveData() {
    binding.viewmodel?.run {
      color.observe(viewLifecycleOwner, Observer {
        val bg = binding.noteColor.background as GradientDrawable
        bg.setColor(it)
      })
      showSnackBarEvent.observe(viewLifecycleOwner, Observer {
        view?.showSnackbar(it, Snackbar.LENGTH_SHORT)
      })
      binding.viewmodel?.trashed?.observe(viewLifecycleOwner, Observer { isTrash ->
        //    回收站中的笔记无法进入编辑页，无法添加提醒
        if (isTrash) {
          iv_no_alarm.isClickable = false
          iv_alarm.isClickable = false
          iv_alarmed.isClickable = false
          activity?.findViewById<View>(R.id.fab_edit_note)?.visibility = View.GONE
        }
        else {
          iv_no_alarm.isClickable = true
          iv_alarm.isClickable = true
          iv_alarmed.isClickable = true
          activity?.findViewById<View>(R.id.fab_edit_note)?.visibility = View.VISIBLE
        }
      })
    }
  }

  private fun setupFAB() {
    activity?.findViewById<View>(R.id.fab_edit_note)?.setOnClickListener {
      binding.viewmodel?.editNote()
    }
  }


  companion object {
    const val ARGUMENT_NOTE_ID = "NOTE_ID"

    fun newInstance(noteId: String) = NoteDetailFragment().apply {
      arguments = Bundle().apply {
        putString(ARGUMENT_NOTE_ID, noteId)
      }
    }
  }
}