package org.wangyichen.anynote.widget

import android.net.Uri
import android.os.Bundle
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_show_cover_image.*
import org.wangyichen.anynote.R
import org.wangyichen.anynote.base.BaseActivity

class ShowCoverImageActivity : BaseActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_show_cover_image)

    val uri: Uri = intent.getParcelableExtra(EXTRA_URI)!!
    Glide.with(this).load(uri).into(cover)

    cover.setOnClickListener {
      this.onBackPressed()
    }
  }

  companion object {
    const val EXTRA_URI = "EXTRA_URI"
  }
}