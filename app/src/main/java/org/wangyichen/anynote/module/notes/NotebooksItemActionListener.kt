package org.wangyichen.anynote.module.notes

import org.wangyichen.anynote.data.Entity.Notebook

interface NotebooksItemActionListener {
  fun onClick(case: Int, notebook: Notebook)
  fun onLongClicked(case: Int,notebook: Notebook):Boolean
}