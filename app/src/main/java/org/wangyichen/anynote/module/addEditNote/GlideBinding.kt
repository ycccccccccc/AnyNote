package org.wangyichen.anynote.module.addEditNote

import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

object GlideBinding {
  @BindingAdapter("android:uri")
  @JvmStatic fun loadImage(view: View, uri: Uri?) {
    if (uri == null || uri.equals(Uri.EMPTY)) return
    Glide.with(view).load(uri).centerCrop().into(view as ImageView)
  }
}