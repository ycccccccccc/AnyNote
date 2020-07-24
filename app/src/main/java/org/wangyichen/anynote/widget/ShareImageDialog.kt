package org.wangyichen.anynote.widget

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialog_share_image.*
import org.wangyichen.anynote.R
import org.wangyichen.anynote.utils.ConfermDialogFragment
import org.wangyichen.anynote.utils.SystemUtils

class ShareImageDialog(val bitmap: Bitmap, val listener: ConfermDialogFragment.ConfermListener) :
  DialogFragment() {

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.dialog_share_image, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    iv_snapshot.setImageBitmap(bitmap)
    cancel.setOnClickListener {
      listener.onNegtive()
      dismiss()
    }
    share.setOnClickListener {
      listener.onPositive()
      dismiss()
    }
  }

  override fun onResume() {
    super.onResume()
    val width =
      SystemUtils.getWidthByPercentage(getString(R.string.share_image_dialog_width).toFloat())
    val height =
      SystemUtils.getHeightByPercentage(getString(R.string.share_image_dialog_height).toFloat())
    dialog?.window?.setLayout(width, height)
  }

  companion object {
    fun getInstance(bitmap: Bitmap, listener: ConfermDialogFragment.ConfermListener) =
      ShareImageDialog(bitmap, listener)
  }
}