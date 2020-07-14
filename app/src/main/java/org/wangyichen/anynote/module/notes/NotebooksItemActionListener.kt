package org.wangyichen.anynote.module.notes

import android.view.View
import org.wangyichen.anynote.source.Entity.Notebook

interface NotebooksItemActionListener {
  fun onClick(case: Int, notebook: Notebook)
  fun onLongClicked(case: Int,notebook: Notebook):Boolean
}