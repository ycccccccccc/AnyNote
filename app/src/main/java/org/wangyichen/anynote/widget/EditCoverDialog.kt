package org.wangyichen.anynote.widget

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.dialog_edit_cover.*
import org.wangyichen.anynote.R
import org.wangyichen.anynote.utils.ConfermDialogFragment
import org.wangyichen.anynote.utils.SystemUtils

class EditCoverDialog : DialogFragment() {
  lateinit var listener: ConfermDialogFragment.ConfermListener
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val view = inflater.inflate(R.layout.dialog_edit_cover,container,false)
    return view
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val uri = arguments?.getParcelable<Uri>(EXTRA_URI)
    Glide.with(this).load(uri).into(cover)

    positive.setOnClickListener {
      listener.onPositive()
      dismiss()
    }
    negative.setOnClickListener {
      listener.onNegtive()
      dismiss()
    }
  }

  override fun onResume() {
    super.onResume()
    val width =
      SystemUtils.getWidthByPercentage(getString(R.string.edit_cover_dialog_width).toFloat())
    val height =
      SystemUtils.getHeightByPercentage(getString(R.string.edit_cover_dialog_height).toFloat())
    dialog?.window?.setLayout(width, height)
  }

  companion object{
    const val EXTRA_URI = "EXTRA_URI"
    fun newInstance(uri: Uri,listener: ConfermDialogFragment.ConfermListener)  = EditCoverDialog().apply {
       arguments= Bundle().apply {
        putParcelable(EXTRA_URI,uri)
      }
      this.listener = listener
    }
  }
}