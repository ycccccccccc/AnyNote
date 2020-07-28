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
import org.wangyichen.anynote.utils.ConfirmDialogFragment
import org.wangyichen.anynote.utils.SystemUtils

//  cover 修改弹窗
class EditCoverDialog : DialogFragment() {
  lateinit var listener: ConfirmDialogFragment.ConfirmListener
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.dialog_edit_cover, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val uri = arguments?.getParcelable<Uri>(EXTRA_URI)
    Glide.with(this).load(uri).into(cover)

    //  更改
    positive.setOnClickListener {
      listener.onPositive()
      dismiss()
    }
    //  删除
    negative.setOnClickListener {
      listener.onNegative()
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

  companion object {
    const val EXTRA_URI = "EXTRA_URI"
    fun newInstance(uri: Uri, listener: ConfirmDialogFragment.ConfirmListener) =
      EditCoverDialog().apply {
        arguments = Bundle().apply {
          putParcelable(EXTRA_URI, uri)
        }
        this.listener = listener
      }
  }
}